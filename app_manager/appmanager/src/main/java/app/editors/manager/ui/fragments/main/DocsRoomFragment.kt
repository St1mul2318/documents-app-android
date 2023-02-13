package app.editors.manager.ui.fragments.main

import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.forEach
import app.editors.manager.R
import app.editors.manager.mvp.models.filter.RoomFilterType
import app.editors.manager.ui.activities.main.ShareActivity
import app.editors.manager.ui.dialogs.AddRoomBottomDialog
import app.editors.manager.ui.dialogs.ContextBottomDialog
import app.editors.manager.ui.popup.MainActionBarPopup
import app.editors.manager.ui.popup.SelectActionBarPopup
import lib.toolkit.base.managers.utils.UiUtils
import lib.toolkit.base.ui.popup.ActionBarPopupItem

class DocsRoomFragment : DocsCloudFragment() {

    private val isRoom get() = cloudPresenter.isCurrentRoom && cloudPresenter.isRoot

    override fun onActionDialog(isThirdParty: Boolean, isDocs: Boolean) {
        if (isRoom) {
            AddRoomBottomDialog().apply {
                onClickListener = object : AddRoomBottomDialog.OnClickListener {
                    override fun onActionButtonClick(roomType: Int) {
                        UiUtils.showEditDialog(
                            context = requireContext(),
                            title = getString(R.string.dialog_create_room),
                            value = getString(R.string.dialog_create_room_value),
                            acceptListener = { title ->
                                cloudPresenter.createRoom(title, roomType)
                            },
                            requireValue = true
                        )
                    }

                    override fun onActionDialogClose() {
                        this@DocsRoomFragment.onActionDialogClose()
                    }

                }
            }.show(parentFragmentManager, AddRoomBottomDialog.TAG)
        } else {
            super.onActionDialog(isThirdParty, isDocs)
        }
    }

    override fun showMainActionBarMenu(excluded: List<ActionBarPopupItem>) {
        if (!presenter.isSelectionMode) {
            if (isRoom) {
                MainActionBarPopup(
                    context = requireContext(),
                    section = presenter.getSectionType(),
                    clickListener = mainActionBarClickListener,
                    sortBy = presenter.preferenceTool.sortBy.orEmpty(),
                    isAsc = isAsc,
                    excluded = excluded
                ).show(requireActivity().window.decorView)
            } else {
                MainActionBarPopup(
                    context = requireContext(),
                    section = -1,
                    clickListener = mainActionBarClickListener,
                    sortBy = presenter.preferenceTool.sortBy.orEmpty(),
                    isAsc = isAsc,
                    excluded = excluded
                ).show(requireActivity().window.decorView)
            }
        } else super.showMainActionBarMenu(excluded)
    }

    override fun showSelectedActionBarMenu(excluded: List<ActionBarPopupItem>) {
        return if (isRoom) {
            super.showSelectedActionBarMenu(
                excluded = listOf(
                    SelectActionBarPopup.Move,
                    SelectActionBarPopup.Copy,
                    SelectActionBarPopup.Download
                )
            )
        } else super.showSelectedActionBarMenu(excluded)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbar_selection_archive -> cloudPresenter.archiveSelectedRooms()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStateMenuSelection() {
        if (isRoom) {
            menuInflater?.inflate(R.menu.docs_select_room, menu)
            menu?.forEach { menuItem ->
                menuItem.isVisible = true
                UiUtils.setMenuItemTint(requireContext(), menuItem, lib.toolkit.base.R.color.colorPrimary)
            }
            setAccountEnable(false)
        } else super.onStateMenuSelection()
    }

    override fun onContextButtonClick(buttons: ContextBottomDialog.Buttons?) {
        when (buttons) {
            ContextBottomDialog.Buttons.ARCHIVE -> {
                cloudPresenter.archiveRoom()
            }
            ContextBottomDialog.Buttons.PIN -> {
                cloudPresenter.pinRoom()
            }
            ContextBottomDialog.Buttons.INFO -> {
                ShareActivity.show(this, cloudPresenter.itemClicked, true)
            }
            ContextBottomDialog.Buttons.SHARE -> {
                ShareActivity.show(this, cloudPresenter.itemClicked, false)
            }
            else -> super.onContextButtonClick(buttons)
        }
    }

    override fun getFilters(): Boolean {
        return if (isRoom) {
            val filter = presenter.preferenceTool.filter
            filter.roomType != RoomFilterType.None || filter.author.id.isNotEmpty()
        } else super.getFilters()
    }

    companion object {

        fun newInstance(stringAccount: String, section: Int, rootPath: String): DocsCloudFragment {
            return DocsRoomFragment().apply {
                arguments = Bundle(3).apply {
                    putString(KEY_ACCOUNT, stringAccount)
                    putString(KEY_PATH, rootPath)
                    putInt(KEY_SECTION, section)
                }
            }
        }
    }

}