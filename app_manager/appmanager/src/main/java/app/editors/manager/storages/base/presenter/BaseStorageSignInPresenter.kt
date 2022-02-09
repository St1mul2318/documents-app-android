package app.editors.manager.storages.base.presenter

import android.accounts.Account
import app.documents.core.account.CloudAccount
import app.editors.manager.app.App
import app.editors.manager.mvp.presenters.base.BasePresenter
import app.editors.manager.mvp.views.base.BaseView
import app.editors.manager.storages.base.view.BaseStorageSignInView
import app.editors.manager.storages.onedrive.mvp.views.OneDriveSignInView
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lib.toolkit.base.R
import lib.toolkit.base.managers.utils.AccountData
import lib.toolkit.base.managers.utils.AccountUtils

open class BaseStorageSignInPresenter<view: BaseStorageSignInView>: BasePresenter<view>() {

    var disposable: Disposable? = null

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    fun saveAccount(cloudAccount: CloudAccount, accountData: AccountData, accessToken: String) {
        val account = Account(cloudAccount.getAccountName(), context.getString(R.string.account_type))

        if (AccountUtils.addAccount(context, account, "", accountData)) {
            addAccountToDb(cloudAccount)
        } else {
            AccountUtils.setAccountData(context, account, accountData)
            AccountUtils.setPassword(context, account, accessToken)
            addAccountToDb(cloudAccount)
        }
        AccountUtils.setToken(context, account, accessToken)
    }

    private fun addAccountToDb(cloudAccount: CloudAccount) {
        CoroutineScope(Dispatchers.Default).launch {
            accountDao.getAccountOnline()?.let {
                accountDao.addAccount(it.copy(isOnline = false))
            }
            accountDao.addAccount(cloudAccount.copy(isOnline = true))
            withContext(Dispatchers.Main) {
                viewState.onLogin()
            }
        }
    }

}