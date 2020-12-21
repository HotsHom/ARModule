package com.module.qrmodule.presentation.main

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.google.ar.core.Session
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.ux.ArFragment
import com.module.qrmodule.R
import com.module.qrmodule.presentation.common.CameraPermissionHelper
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject


class MainActivity : MvpAppCompatActivity(), IMainView {

    private var arFragment: ArFragment? = null
    private var fitToScanView: ImageView? = null

    @Inject
    @InjectPresenter
    lateinit var presenter: MainPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        arFragment = sceneform_fragment as ArFragment
        presenter.context = this
        arFragment?.arSceneView?.planeRenderer?.isEnabled = false
        fitToScanView = image_view_fit_to_scan
        fitToScanView?.setImageBitmap(BitmapFactory.decodeStream(assets.open("fit_to_scan.png")))
    }

    override fun onResume() {
        super.onResume()
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            CameraPermissionHelper.requestCameraPermission(this);
            return
        }
        if (presenter.session == null)
            presenter.createSession()
        showScanCorner(false)
    }

    override fun showScanCorner(enable: Boolean) {
        fitToScanView?.visibility = if(enable) View.VISIBLE else View.GONE
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(this, "Разрешения камеры нужны для работы приложения", Toast.LENGTH_LONG).show()
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                CameraPermissionHelper.launchPermissionSettings(this)
            }
            finish()
        }
    }

    override fun setupSession(session: Session) {
        arFragment?.arSceneView?.setupSession(session)

        // Устанавливаем прослушку на обновление сцены
        arFragment?.arSceneView?.scene?.apply {
            addOnUpdateListener { presenter.onUpdateFrame(it) }
        }
    }

    override fun removeChildFromScene(anchorNode: AnchorNode) {
        arFragment?.arSceneView?.scene?.removeChild(anchorNode)
    }


    override fun addChildIntoScene(anchorNode: AnchorNode) {
        arFragment?.arSceneView?.scene?.addChild(anchorNode)
    }

}