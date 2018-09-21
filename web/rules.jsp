<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.google.gson.JsonArray" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp" %>

<html>
<head>
    <title>规则</title>
    <style>
        .ui-autocomplete {
            display: block;
            z-index: 99999;
        }
    </style>
</head>
<body>

<%

    Object object = session.getAttribute("isAdmin");
    if (object == null) {
        response.sendRedirect("login.jsp");
    }

    List<JSObject> allTags = Tags.fetchAllTags();
    JsonArray array = new JsonArray();
    for (int i = 0; i < allTags.size(); i++) {
        array.add((String) allTags.get(i).get("tag_name"));
    }
%>

<div class="container-fluid">
    <%@include file="common/navigationbar.jsp" %>

    <div class="panel panel-default">
        <!-- Default panel contents -->
        <div class="panel-heading">
            <a href="campain_monitor_log.jsp" target="_blank">关停记录</a>
            <button id="btn_add_new_rule" class="btn btn-default">添加</button>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <label for="ruleType">规则类型</label>
            <select class="selectpicker" id="ruleType">
                <option value="">所有类型</option>
                <option value="1">类型1（监控广告系列）</option>
                <option value="2">类型2（监控应用）</option>
                <option value="3">类型3（监控国家花费）</option>
                <option value="4">类型4（监控国家回本率）</option>
                <option value="5">类型5（监控所有国家回本率）</option>
            </select>
            <label for="inputTag">标签</label>
            <input id="inputTag" type="text"/>
            <label for="inputQueryText">规则内容</label>
            <input id="inputQueryText" type="text"/>
            <button id="btnQuery" class="btn btn-default glyphicon glyphicon-search"></button>
        </div>

        <table class="table">
            <thead>
            <tr>
                <th>序号</th>
                <th>规则类型</th>
                <th>内容</th>
                <th>标签序号</th>
                <th>标签名称</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>

            </tbody>
        </table>

        <nav aria-label="Page navigation">
            共 <span id="totalData">0</span> 条数据
        </nav>
    </div>
</div>

<div id="new_rule_dlg" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="dlg_title">添加规则</h4>
            </div>
            <div class="modal-body">
                <form id="modify_form" class="form-horizontal" action="#" autocomplete="off">
                    <div class="form-group">
                        <label for="inputRuleType" class="col-sm-2 control-label">规则类型</label>
                        <div class="col-sm-10">
                            <select class="form-control" id="inputRuleType">
                                <option value="1">类型1（监控广告系列）</option>
                                <option value="2">类型2（监控应用）</option>
                                <option value="3">类型3（监控国家花费）</option>
                                <option value="4">类型4（监控国家回本率）</option>
                                <option value="5">类型5（监控所有国家回本率）</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="inputRuleContent" class="col-sm-2 control-label">缩写</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control" id="inputRuleContent" placeholder="规则内容"
                                   autocomplete="off">
                        </div>
                    </div>

                    <div class="form-group" id="inputSearchDiv">
                        <label for="inputSearch" class="col-sm-2 control-label">标签名称</label>
                        <div class="col-sm-10">
                            <input id="inputSearch" class="form-control" style="display: inline; width: auto;" type="text"/>
                        </div>
                    </div>

                </form>
                <p id="delete_message">确认要删除吗?</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary">确定</button>
            </div>
        </div>
    </div>
</div>

<jsp:include page="common/loading_dialog.jsp"></jsp:include>

<script type="text/javascript">
    var data = <%=array.toString()%>;
    $("#inputTag").autocomplete({
        source: data
    });
    $("#inputSearch").autocomplete({
        source: data
    });

    var modifyType = 'new';
    var id;
    $("li[role='presentation']:eq(4)").addClass("active");

    $("#btn_add_new_rule").click(function () {
        document.getElementById("modify_form").reset();
        $('#inputRuleContent').val("appName=VpnV10,countryCode=US,CPA<0.3,costDivBudget>0.9,budget+=CPA*40")
        modifyType = 'new';
        $('#delete_message').hide();
        $('#inputSearchDiv').show();
        $('#modify_form').show();
        $("#dlg_title").text("添加规则");
        $("#new_rule_dlg").modal("show");
    });
    $('#inputRuleContent').attr('placeholder', "campaign_id=xxx,conversions>xxx,cpa>xxx");

    $('#inputRuleType').change(function () {
        var ruleType = $('#inputRuleType').val();
        if (ruleType == 1) {
            $('#inputRuleContent').val("appName=VpnV10,countryCode=US,CPA<0.3,costDivBudget>0.9,budget+=CPA*40")
        } else if (ruleType == 2) {//是应用维度    app_name=应用名称,cost>花费数字,cpa>cpa值
            $('#inputRuleContent').val("app_name=xxx,cost>xxx,cpa>xxx")
        } else if (ruleType == 3) {//是应用+国家维度 app_name=应用名称,country_code=国家代号,cpa_div_ecpm>cpa除以ecpm,cost>花费
            $('#inputRuleContent').val("app_name=xxx,country_code=xxx,cpa_div_ecpm>xxx,cost>xxx")
        } else if (ruleType == 4) {
            $('#inputRuleContent').val("app_name=xxx,country_code=xxx,purchased_user>10,roi<0.5")
        } else if (ruleType == 5) {
            $('#inputRuleContent').val("app_name=xxx,purchased_user>10,roi<0.5")
        }
    });

    /**
     * 检验规则语法是否正确
     * @returns {boolean}
     */
    function validData() {
        var ruleType = $("#inputRuleType").val();
        var ruleContent = $("#inputRuleContent").val();
        // ruleContent = ruleContent.toLowerCase().replace(/\s/g, "").replace(/xxx/g, "");

        ruleContent = ruleContent.replace(/\s/g, "").replace(/xxx/g, "");

        var ruleParam = ruleContent.split(",");
        var rule = {};
        for (var i = 0; i < ruleParam.length; i++) {
            var item = ruleParam[i].replace(">", "=").replace("<", "=").split("=");
            rule[item[0]] = item[1];
        }
        console.info(rule);

        var checkFlag = true;

        if (ruleType == 1) { //系列维度

        } else if (ruleType == 2) {//是应用维度    app_name=应用名称,cost>花费数字,cpa>cpa值
            if (!checkNum(rule["cost"]) || !checkNum(rule["cpa"])) {
                checkFlag = false;
                alert("cost、cpa 必须为正数数字");
            }
        } else if (ruleType == 3) {//是应用+国家维度 app_name=应用名称,country_code=国家代号,cpa_div_ecpm>cpa除以ecpm,cost>花费
            //$('#inputRuleContent').val("app_name=xxx,country_code=xxx,cpa_div_ecpm>xxx,cost>xxx");
            if (!checkNum(rule["cost"]) || !checkNum(rule["cpa_div_ecpm"])) {
                checkFlag = false;
                alert("cost、cpa_div_ecpm 必须为正数数字");
            }
            var reg = /^[A-Z]{2}$/;
            if (!reg.test(rule["country_code"])) {
                checkFlag = false;
                alert("country_code必须为2位大写字母");
            }
        }else if (ruleType == 4) {
            if (!checkNum(rule["purchased_user"]) || !checkNum(rule["roi"])) {
                checkFlag = false;
                alert("purchased_user、roi 必须为正数数字");
            }
            var reg = /^[A-Z]{2}$/;
            if (!reg.test(rule["country_code"])) {
                checkFlag = false;
                alert("country_code必须为2位大写字母");
            }

        }else if (ruleType == 5) {
            if (!checkNum(rule["purchased_user"]) || !checkNum(rule["roi"])) {
                checkFlag = false;
                alert("purchased_user、roi 必须为正数数字");
            }
        }
        return checkFlag;
    }


    function checkNum(theObj) {//校验为正数
        var reg = /^\d+(\.\d+)?$/;
        return reg.test(theObj);
    }

    $("#new_rule_dlg .btn-primary").click(function () {
        if (!validData()) {//规则语法不正确
            return false;
        }
        var ruleType = $("#inputRuleType").val();
        var ruleContent = $("#inputRuleContent").val();

        if (modifyType == 'new') {

            var tag_name1 = $("#inputSearch").val();

            $.post('rules/selectTagId', {
                tag_name: tag_name1
            }, function (data) {
                var tag_id = data.data.id;
                $.post('rules/create', {
                    ruleType: ruleType,
                    ruleContent: ruleContent,
                    tag_id:tag_id,
                    tag_name:tag_name1
                }, function (data) {
                    if (data && data.ret == 1) {
                        $("#new_rule_dlg").modal("hide");
                    } else {
                        admanager.showCommonDlg("错误", data.message);
                    }
                }, 'json');
            }, 'json');

        } else if (modifyType == 'update') {
            $.post('rules/update', {
                id: id,
                ruleType: ruleType,
                ruleContent: ruleContent
            }, function (data) {
                if (data && data.ret == 1) {
                    $("#new_rule_dlg").modal("hide");
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        } else if (modifyType == 'delete') {
            $.post('rules/delete', {
                id: id,
                ruleType: ruleType,
                ruleContent: ruleContent
            }, function (data) {
                if (data && data.ret == 1) {
                    $("#new_rule_dlg").modal("hide");
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        }
        doResearch();
    })
    ;

    function bindOp() {
        $(".link_modify").click(function () {
            document.getElementById("modify_form").reset();
            modifyType = "update";
            $('#delete_message').hide();
            $('#inputSearchDiv').hide();
            $('#modify_form').show();

            $("#dlg_title").text("修改规则");

            var tds = $(this).parents("tr").find('td');
            id = $(tds.get(0)).text();
            var ruleType = $(tds.get(1)).text();
            var ruleContent = $(tds.get(2)).text();
            $("#inputRuleType").val(ruleType);
            $("#inputRuleContent").val(ruleContent);

            $("#new_rule_dlg").modal("show");
        });

        $(".link_delete").click(function () {
            modifyType = "delete";
            $('#delete_message').show();
            $('#modify_form').hide();

            $("#dlg_title").text("删除规则");

            var tds = $(this).parents("tr").find('td');
            id = $(tds.get(0)).text();
            var ruleType = $(tds.get(1)).text();
            var ruleContent = $(tds.get(2)).text();
            $("#inputRuleType").val(ruleType);
            $("#inputRuleContent").val(ruleContent);

            $("#new_rule_dlg").modal("show");
        });
    }

    $('#btnQuery').click(doResearch);

    function doResearch() {
        var query = $('#inputQueryText').val();
        var ruleType = $('#ruleType').val();
        var tagName = $('#inputTag').val();
        $('.table tbody ').html("");
        $.post('rules/query', {
            ruleType:ruleType,
            tagName:tagName,
            ruleText: query
        }, function (data) {
            if (data && data.ret == 1) {
                $("#totalData").text(data.data.length);
                for (var i = 0; i < data.data.length; i++) {
                    var one = data.data[i];
                    var tr = $('<tr></tr>');

                    var td = $('<td></td>');
                    td.text(one.id);
                    tr.append(td);

                    td = $('<td></td>');
                    td.text(one.rule_type);
                    tr.append(td);

                    td = $('<td></td>');
                    td.text(one.rule_content);
                    tr.append(td);

                    td = $('<td></td>');
                    td.text(one.tag_id3);
                    tr.append(td);

                    td = $('<td></td>');
                    td.text(one.tag_name3);
                    tr.append(td);

                    td = $('<td><a class="link_modify glyphicon glyphicon-pencil" href="#"></a>&nbsp;&nbsp;&nbsp;&nbsp;' +
                        '<a class="link_delete glyphicon glyphicon-remove" href="#"></a></td>');
                    tr.append(td);
                    $('.table tbody').append(tr);
                }
                bindOp();
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');
    }

</script>
<script src="js/interlaced-color-change.js"></script>
</body>
</html>
