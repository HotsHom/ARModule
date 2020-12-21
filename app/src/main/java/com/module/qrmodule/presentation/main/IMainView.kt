package com.module.qrmodule.presentation.main

import com.google.ar.core.Session
import com.google.ar.sceneform.AnchorNode
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(SkipStrategy::class)
interface IMainView: MvpView {
    fun setupSession(session: Session)
    fun removeChildFromScene(anchorNode: AnchorNode)
    fun addChildIntoScene(anchorNode: AnchorNode)
    fun showScanCorner(enable: Boolean)
}