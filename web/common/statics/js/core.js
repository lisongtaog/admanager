/**
 * Created by jikai on 5/16/17.
 */

var admanager = {};

admanager.showCommonDlg = function(title, message, callback) {
    //$('#common_message_dialog').modal('show');
    var config = {
        backdrop : 'static',//默认为true
        keyboard: true,//默认为true
        show: true
    };

    $('#common_message_dialog').modal(config);
    $("#common_dlg_title").text(title);
    //换行的格式化输出
    if( typeof message === 'string' && message.indexOf("\n") > -1 ){
        $("#common_dlg_message").html("").append(
            $("<pre>").html(message)
        );
    }else{
        $("#common_dlg_message").text(message);
    }

    $("#common_message_dialog").off("hidden.bs.modal");
    $("#common_message_dialog").on("hidden.bs.modal", function(e) {
        if (callback) callback();
    });
}

admanager.hideCommonDlg = function(callback) {
    $("#common_message_dialog").modal("hide");
    if (callback) callback();
}

function popupCenter(url, title, w, h) {
    // Fixes dual-screen position                         Most browsers      Firefox
    var dualScreenLeft = window.screenLeft != undefined ? window.screenLeft : screen.left;
    var dualScreenTop = window.screenTop != undefined ? window.screenTop : screen.top;

    var width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
    var height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;

    var left = ((width / 2) - (w / 2)) + dualScreenLeft;
    var top = ((height / 2) - (h / 2)) + dualScreenTop;
    var newWindow = window.open(url, title, 'scrollbars=yes, width=' + w + ', height=' + h + ', top=' + top + ', left=' + left);

    // Puts focus on the newWindow
    if (window.focus) {
        newWindow.focus();
    }
}