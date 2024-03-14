package app.editors.manager.mvp.presenters.login

import app.documents.core.model.cloud.CloudPortal
import app.documents.core.model.cloud.PortalProvider
import app.documents.core.network.common.contracts.ApiContract
import app.editors.manager.app.App
import moxy.InjectViewState

@InjectViewState
class PersonalLoginPresenter : EnterpriseLoginPresenter() {

    companion object {
        val TAG: String = PersonalLoginPresenter::class.java.simpleName
    }

    init {
        App.getApp().appComponent.inject(this)
    }

    fun signInPersonal(login: String, password: String) {
        App.getApp().refreshLoginComponent(
            CloudPortal(
                url = ApiContract.PERSONAL_HOST,
                provider = PortalProvider.Cloud.Personal
            )
        )
        signInPortal(login.trim { it <= ' ' }, password, ApiContract.PERSONAL_HOST)
    }

}