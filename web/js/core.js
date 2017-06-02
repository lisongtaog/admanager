/**
 * Created by jikai on 5/16/17.
 */

var admanager = {};

admanager.showCommonDlg = function(title, message, callback) {
    $('#common_message_dialog').modal('show');
    $("#common_dlg_title").text(title);
    $("#common_dlg_message").text(message);

    $("#common_message_dialog").on("hide.bs.modal", function(e) {
        if (callback) callback();
    });
}