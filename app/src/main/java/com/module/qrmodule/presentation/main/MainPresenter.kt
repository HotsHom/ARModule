package com.module.qrmodule.presentation.main

import android.app.AlertDialog
import android.content.Context
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ar.core.*
import com.google.ar.core.exceptions.NotYetAvailableException
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.FixedHeightViewSizer
import com.google.ar.sceneform.rendering.ViewRenderable
import com.module.qrmodule.R
import com.module.qrmodule.domain.ImageDatabase
import com.module.qrmodule.domain.data.Port
import com.module.qrmodule.presentation.common.ImageHelper
import com.module.qrmodule.presentation.common.QRHelper
import com.module.qrmodule.presentation.common.portList.PortAdapter
import moxy.InjectViewState
import moxy.MvpPresenter
import java.util.*
import javax.inject.Inject

@InjectViewState
class MainPresenter @Inject constructor() : MvpPresenter<IMainView>() {

    var context: Context? = null
    var session: Session? = null
    private var scene: Scene? = null
    private var isObjectPlaced = false
    private var textMessage: String = ""
    private var isPlaneTrackingStop: Boolean = false
    private var ui: ViewRenderable? = null
    private var adapterView: PortAdapter? = null
    private var sessionConfig: Config? = null
    private var imageDatabase: ImageDatabase? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showScanCorner(false)
    }

    fun createSession() {
        // Создание сессии
        session = Session(context).apply {

            // Конфиг для камеры
            val filter = CameraConfigFilter(this).apply {
                depthSensorUsage = EnumSet.of(CameraConfig.DepthSensorUsage.DO_NOT_USE)
                stereoCameraUsage = EnumSet.of(CameraConfig.StereoCameraUsage.DO_NOT_USE)
                targetFps = EnumSet.of(CameraConfig.TargetFps.TARGET_FPS_30)
            }
            this.cameraConfig = this.getSupportedCameraConfigs(filter).first()

            // Устанавливаем базу данных распознавания изображений
            imageDatabase = context?.let { ImageDatabase(this, it) }

            // Конфигурация сессии
            sessionConfig = Config(this).apply {
                augmentedImageDatabase = imageDatabase?.getImageDatabase()
                focusMode = Config.FocusMode.AUTO
                updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                cloudAnchorMode = Config.CloudAnchorMode.DISABLED
                augmentedFaceMode = Config.AugmentedFaceMode.DISABLED
                lightEstimationMode = Config.LightEstimationMode.DISABLED
            }
            configure(sessionConfig)
            viewState.setupSession(this)
        }
    }

    // Обновления сцены
    fun onUpdateFrame(frameTime: FrameTime) {
        if ( isObjectPlaced) return
        val frame = session?.update()

        frame?.getUpdatedTrackables(Plane::class.java)?.forEach {
            if (it.trackingState == TrackingState.TRACKING) {
                isPlaneTrackingStop = true
                viewState.showScanCorner(ui == null)
            }
        }

        if (!isPlaneTrackingStop) return

        val updatedAugmentedImages = frame?.getUpdatedTrackables(AugmentedImage::class.java)

        updatedAugmentedImages?.forEach { augmentedImage ->
            if (augmentedImage.trackingState == TrackingState.TRACKING) {
                if (augmentedImage.trackingMethod == AugmentedImage.TrackingMethod.FULL_TRACKING) {
                    viewState.showScanCorner(ui == null)
                    if (isObjectPlaced)
                        return

                    try {
                        frame.acquireCameraImage().apply {
                            textMessage = QRHelper().getResult(ImageHelper().Image2Bitmap(this))
                                ?: return@apply
                        }.close()

                        if (textMessage == "") return

                        if (textMessage.isNotBlank()) {
                            val anchorNode =
                                AnchorNode(augmentedImage.createAnchor(augmentedImage.centerPose))
                            viewState.removeChildFromScene(anchorNode)
                            getUI(augmentedImage.extentX)

                            AnchorNode().apply {
                                renderable = ui ?: return
                                setLookDirection(Vector3.down(), Vector3.up())
                                val pose = Pose.makeTranslation(-0.025f, 0.0f, 0.025f)
                                localPosition = Vector3(pose.tx(), pose.ty(), pose.tz())
                                setParent(anchorNode)
                            }

                            viewState.addChildIntoScene(anchorNode)
                            Toast.makeText(context, textMessage, Toast.LENGTH_SHORT).show()
                            isObjectPlaced = true
                        } else {
                            Toast.makeText(
                                context,
                                "Поднесите телефон ближе к метке",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (ex: NotYetAvailableException) {
                        ex.printStackTrace()
                    }
                }
            }
        }
    }

    private fun getUI(extentX: Float) {
        ViewRenderable.builder()
            .setView(context, R.layout.controls)
            .build()
            .thenAccept {
                it.isShadowCaster = false
                it.isShadowReceiver = false
                it.horizontalAlignment = ViewRenderable.HorizontalAlignment.LEFT
                it.sizer = FixedHeightViewSizer(extentX)

                it?.view?.findViewById<TextView>(R.id.tvText)?.text = textMessage
                it?.view?.findViewById<RecyclerView>(R.id.rvList)?.apply {
                    layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
                    adapter = PortAdapter(getTestPortList())
                    adapterView = adapter as PortAdapter
                    it.view?.findViewById<LinearLayout>(R.id.llMain)?.layoutParams?.apply {
                        this.width = (adapter?.itemCount?.times(260))?.plus(260) ?: 520
                    }
                    it.view?.findViewById<LinearLayout>(R.id.llMain)?.requestLayout()
                    it.view?.findViewById<ImageButton>(R.id.ibRefresh)?.setOnClickListener { refreshPort(adapter as PortAdapter) }
                }

                ui = it
            }
            .exceptionally {
                val builder = AlertDialog.Builder(context)
                builder.setMessage(it.message).setTitle("Error")
                val dialog = builder.create()
                dialog.show()
                return@exceptionally null
            }
    }

    private fun getTestPortList(): ArrayList<Port> {
        val data = arrayListOf<Port>()
        (0..9).forEach { i ->
            val isDisable = (0..1).random() == 0
            data.add( Port(id = i + 1, isDisable = isDisable) )
        }
        return data
    }

    private fun refreshPort(adapter: PortAdapter) {
        adapter.setData(getTestPortList())
    }
}