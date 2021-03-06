
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp"%>

<html>
<head>
    <title>创建系列统计</title>

    <style>
        td.editable {

        }
        td.editable.checkbox {

        }
        td.changed {
            background-color: #0f0;
        }
        #total_result.editable {
            background-color: yellow;
        }
        .estimateResult {
            color: red;
        }
        .red {
            color: red;
        }
        .blue {
            color: #0f0;
        }
        .yellow{
            color: #ffa17a;
        }
    </style>
</head>
<body>

<%

    Object object = session.getAttribute("isAdmin");
    if (object == null) {
        response.sendRedirect("login.jsp");
    }
%>

<div class="container-fluid">
    <div class="panel panel-default" style="margin-top: 10px">
        <div class="panel-heading" id="panel_title">
            <span>系列创建时间</span>
            <input type="text" value="2012-05-15" id="inputCampaignCreateTime" readonly>
            <button id="btnSearch" class="btn btn-default glyphicon glyphicon-search"></button>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-body" id="total_result">
        </div>
    </div>
    <table class="table table-hover">
        <thead id="result_header">
        <tr>
            <th>应用名称</th><th>Facebook手动</th><th>Facebook自动</th><th>Facebook所有</th><th>Adwords手动</th><th>Adwords自动</th><th>Adwords所有</th><th>Facebook+Adwords</th>
        </tr>
        </thead>
        <tbody id="results_body">
        </tbody>
    </table>
</div>

</div>

<jsp:include page="common/loading_dialog.jsp"></jsp:include>

<script>
    function init() {
        var now = new Date(new Date().getTime());
        $('#inputCampaignCreateTime').val(now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate());

        $('#inputCampaignCreateTime').datetimepicker({
            minView: "month",
            format: 'yyyy-mm-dd',
            autoclose: true,
            todayBtn: true
        });
        $('#btnSearch').click();
    }
    $('#btnSearch').click(function () {
        var  campaignCreateTime = $("#inputCampaignCreateTime").val();
        $.post('query_two/query_create_campaign_statistics', {
            campaignCreateTime: campaignCreateTime
        }, function (data) {
            if (data && data.ret == 1) {
                $('#result_header').html("<tr><th>应用名称</th><th>Facebook手动</th><th>Facebook自动</th><th>Facebook所有</th><th>Adwords手动</th><th>Adwords自动</th><th>Adwords所有</th><th>Facebook+Adwords</th></tr>");
                $('#results_body > tr').remove();
                var arr = data.array;
                var len = arr.length;
                for (var i = 0; i < len; i++) {
                    var one = arr[i];
                    var tr = $('<tr></tr>');
                    var keyset = ["app_name", "facebook_hander", "facebook_auto", "facebook_all","adwords_hander", "adwords_auto", "adwords_all", "all"];
                    for (var j = 0; j < keyset.length; j++) {
                        var td = $('<td></td>');
                        td.text(one[keyset[j]]);
                        tr.append(td);
                    }
                    $('#results_body').append(tr);
                }
                var str = "Facebook创建总数: " + data.total_all_f + "  Adwords创建总数: " + data.total_all_a  + "   所有系列创建总数: " + data.total_all;
                str += "<br/><span class='estimateResult'></span>"
                $('#total_result').removeClass("editable");
                $('#total_result').html(str);
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');
    });

    init();
</script>
<script src="js/interlaced-color-change.js"></script>
</body>
</html>
