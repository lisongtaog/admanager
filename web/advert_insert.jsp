<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>广告存储</title>
    <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="bootstrap/css/bootstrap-theme.min.css"/>
    <link rel="stylesheet" href="css/core.css"/>
    <link rel="stylesheet" href="css/bootstrap-tagsinput.css"/>
    <link rel="stylesheet" href="css/bootstrap-datetimepicker.css"/>
    <link rel="stylesheet" href="jqueryui/jquery-ui.css"/>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.4/css/select2.min.css" rel="stylesheet" />
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
    <ul class="nav nav-pills">
        <li role="presentation"><a href="index.jsp">首页</a></li>
        <li role="presentation"><a href="campaigns_create.jsp">创建广告</a></li>
        <li role="presentation"><a href="adaccounts.jsp">广告账号管理</a></li>
        <li role="presentation"><a href="adaccounts_admob.jsp">广告账号管理(AdMob)</a></li>
        <li role="presentation"><a href="campaigns.jsp">广告系列管理</a></li>
        <li role="presentation"><a href="campaigns_admob.jsp">广告系列管理(AdMob)</a></li>
        <li role="presentation"><a href="tags.jsp">标签管理</a></li>
        <li role="presentation"><a href="rules.jsp">规则</a></li>
        <li role="presentation"><a href="query.jsp">查询</a></li>
        <li role="presentation"><a href="system.jsp">系统管理</a></li>
        <li role="presentation"><a href="summary.jsp">七天汇总</a></li>
        <li role="presentation" class="active"><a href="#">广告存储</a></li>
    </ul>

    <div class="panel panel-default" style="margin-top: 10px">
        <div class="panel-heading" id="panel_title">
            <label>
                <input type="radio" name="optionsRadios" id="checkFacebook" checked>
                Facebook 广告
            </label>
            <label>
                <input type="radio" name="optionsRadios" id="checkAdmob">
                AdWords 广告
            </label>
        </div>
    </div>

    <div id="moreCountryDlg" class="modal fade" tabindex="-1" role="dialog">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="dlg_title">输入国家，每行一个</h4>
                </div>
                <div class="modal-body">
                    <form id="modify_form" class="form-horizontal" action="#" autocomplete="off">
                        <div class="form-group">
                            <label for="inputCountryAlias" class="col-sm-2 control-label">国家缩写</label>
                            <div class="col-sm-10">
                                <input id="inputCountryAlias" style="width:100%;" />
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-primary">确定</button>
                </div>
            </div>
        </div>
    </div>

    <form class="form-horizontal" action="#" id="formFacebook">
        <div class="form-group">
            <label for="selectApp" class="col-sm-2 control-label">应用</label>
            <div class="col-sm-10">
                <select class="form-control" id="selectApp">
                </select>
            </div>
        </div>
        <div class="form-group">
            <label for="selectLanguage" class="col-sm-2 control-label">语言</label>
            <div class="col-sm-10">
                <select class="form-control" id="selectLanguage">
                </select>
            </div>
        </div><br>

        <div class="form-group">
            <label for="inputGroupOne" class="col-sm-2 control-label">组合</label>
            <div class="col-sm-1">
                <input type="text" value="1" readonly id="inputGroupOne" style="width: 10px;">
            </div>
        </div>
        <div class="form-group">
            <label for="inputTitle11" class="col-sm-2 control-label">广告标题</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputTitle11" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessage11" class="col-sm-2 control-label">广告语</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessage11" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputGroupTwo" class="col-sm-2 control-label">组合</label>
            <div class="col-sm-2">
                <input type="text" value="2" readonly id="inputGroupTwo" style="width: 20px;">
            </div>
        </div>
        <div class="form-group">
            <label for="inputTitle22" class="col-sm-2 control-label">广告标题</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputTitle22" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessage22" class="col-sm-2 control-label">广告语</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessage22" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputGroupThree" class="col-sm-2 control-label">组合</label>
            <div class="col-sm-3">
                <input type="text" value="3" readonly id="inputGroupThree" style="width: 30px;">
            </div>
        </div>
        <div class="form-group">
            <label for="inputTitle33" class="col-sm-2 control-label">广告标题</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputTitle33" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessage33" class="col-sm-2 control-label">广告语</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessage33" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputGroupFour" class="col-sm-2 control-label">组合</label>
            <div class="col-sm-4">
                <input type="text" value="4" readonly id="inputGroupFour" style="width: 40px;">
            </div>
        </div>
        <div class="form-group">
            <label for="inputTitle44" class="col-sm-2 control-label">广告标题</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputTitle44" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessage44" class="col-sm-2 control-label">广告语</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessage44" />
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-10" style="text-align: center">
                <input type="submit" class="btn btn-primary" id="btnInsert" value="保存"/>
            </div>
        </div>
    </form>


    <form class="form-horizontal" action="#" id="formAdmob">

        <div class="form-group">
            <label for="selectAppAdmob" class="col-sm-2 control-label">应用</label>
            <div class="col-sm-10">
                <select class="form-control" id="selectAppAdmob">

                </select>
            </div>
        </div>

        <div class="form-group">
            <label for="selectLanguageAdmob" class="col-sm-2 control-label">语言</label>
            <div class="col-sm-10">
                <select class="form-control" id="selectLanguageAdmob">
                </select>
            </div>
        </div>

        <div class="form-group">
            <label for="inputGroupOneAdmob10" class="col-sm-2 control-label">组合</label>
            <div class="col-sm-4">
                <input type="text" value="1" readonly id="inputGroupOneAdmob10" style="width: 10px;">
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob11" class="col-sm-2 control-label">广告语1</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob11" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob12" class="col-sm-2 control-label">广告语2</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob12" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob13" class="col-sm-2 control-label">广告语3</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob13" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob14" class="col-sm-2 control-label">广告语4</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob14" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputGroupOneAdmob20" class="col-sm-2 control-label">组合</label>
            <div class="col-sm-4">
                <input type="text" value="2" readonly id="inputGroupOneAdmob20" style="width: 20px;">
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob21" class="col-sm-2 control-label">广告语1</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob21" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob22" class="col-sm-2 control-label">广告语2</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob22" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob23" class="col-sm-2 control-label">广告语3</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob23" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob24" class="col-sm-2 control-label">广告语4</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob24" />
            </div>
        </div>

        <div class="form-group">
            <label for="inputGroupOneAdmob30" class="col-sm-2 control-label">组合</label>
            <div class="col-sm-4">
                <input type="text" value="3" readonly id="inputGroupOneAdmob30" style="width: 30px;">
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob31" class="col-sm-2 control-label">广告语1</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob31" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob32" class="col-sm-2 control-label">广告语2</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob32" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob33" class="col-sm-2 control-label">广告语3</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob33" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob34" class="col-sm-2 control-label">广告语4</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob34" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputGroupOneAdmob40" class="col-sm-2 control-label">组合</label>
            <div class="col-sm-4">
                <input type="text" value="4" readonly id="inputGroupOneAdmob40" style="width: 40px;">
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob41" class="col-sm-2 control-label">广告语1</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob41" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob42" class="col-sm-2 control-label">广告语2</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob42" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob43" class="col-sm-2 control-label">广告语3</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob43" />
            </div>
        </div>
        <div class="form-group">
            <label for="inputMessageAdmob44" class="col-sm-2 control-label">广告语4</label>
            <div class="col-sm-10">
                <input class="form-control" id="inputMessageAdmob44" />
            </div>
        </div>

        <div class="form-group">
            <div class="col-sm-10" style="text-align: center">
                <input type="submit" class="btn btn-primary" id="btnInsertAdmob" value="保存"/>
            </div>
        </div>
    </form>

</div>

</div>

<jsp:include page="loading_dialog.jsp"></jsp:include>


<script src="js/jquery.js"></script>
<script src="bootstrap/js/bootstrap.min.js"></script>
<script src="js/core.js"></script>
<script src="js/bootstrap-datetimepicker.js"></script>
<script src="jqueryui/jquery-ui.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.4/js/select2.min.js"></script>
<script>
    var regionList = [{"name":"Worldwide"},{"key":"AD","name":"Andorra","type":"country","country_code":"AD","supports_region":true,"supports_city":false},{"key":"AE","name":"United Arab Emirates","type":"country","country_code":"AE","supports_region":true,"supports_city":true},{"key":"AF","name":"Afghanistan","type":"country","country_code":"AF","supports_region":true,"supports_city":false},{"key":"AG","name":"Antigua","type":"country","country_code":"AG","supports_region":true,"supports_city":false},{"key":"AI","name":"Anguilla","type":"country","country_code":"AI","supports_region":true,"supports_city":false},{"key":"AL","name":"Albania","type":"country","country_code":"AL","supports_region":true,"supports_city":false},{"key":"AM","name":"Armenia","type":"country","country_code":"AM","supports_region":true,"supports_city":false},{"key":"AN","name":"Netherlands Antilles","type":"country","country_code":"AN","supports_region":false,"supports_city":false},{"key":"AO","name":"Angola","type":"country","country_code":"AO","supports_region":true,"supports_city":false},{"key":"AQ","name":"Antarctica","type":"country","country_code":"AQ","supports_region":false,"supports_city":false},{"key":"AR","name":"Argentina","type":"country","country_code":"AR","supports_region":true,"supports_city":true},{"key":"AS","name":"American Samoa","type":"country","country_code":"AS","supports_region":true,"supports_city":false},{"key":"AT","name":"Austria","type":"country","country_code":"AT","supports_region":true,"supports_city":true},{"key":"AU","name":"Australia","type":"country","country_code":"AU","supports_region":true,"supports_city":true},{"key":"AW","name":"Aruba","type":"country","country_code":"AW","supports_region":false,"supports_city":false},{"key":"AX","name":"Aland Islands","type":"country","country_code":"AX","supports_region":false,"supports_city":false},{"key":"AZ","name":"Azerbaijan","type":"country","country_code":"AZ","supports_region":true,"supports_city":false},{"key":"BA","name":"Bosnia and Herzegovina","type":"country","country_code":"BA","supports_region":false,"supports_city":false},{"key":"BB","name":"Barbados","type":"country","country_code":"BB","supports_region":true,"supports_city":false},{"key":"BD","name":"Bangladesh","type":"country","country_code":"BD","supports_region":true,"supports_city":false},{"key":"BE","name":"Belgium","type":"country","country_code":"BE","supports_region":true,"supports_city":true},{"key":"BF","name":"Burkina Faso","type":"country","country_code":"BF","supports_region":false,"supports_city":false},{"key":"BG","name":"Bulgaria","type":"country","country_code":"BG","supports_region":true,"supports_city":true},{"key":"BH","name":"Bahrain","type":"country","country_code":"BH","supports_region":true,"supports_city":false},{"key":"BI","name":"Burundi","type":"country","country_code":"BI","supports_region":true,"supports_city":false},{"key":"BJ","name":"Benin","type":"country","country_code":"BJ","supports_region":true,"supports_city":false},{"key":"BL","name":"Saint Barthélemy","type":"country","country_code":"BL","supports_region":false,"supports_city":false},{"key":"BM","name":"Bermuda","type":"country","country_code":"BM","supports_region":true,"supports_city":false},{"key":"BN","name":"Brunei","type":"country","country_code":"BN","supports_region":true,"supports_city":false},{"key":"BO","name":"Bolivia","type":"country","country_code":"BO","supports_region":true,"supports_city":true},{"key":"BQ","name":"Bonaire, Sint Eustatius and Saba","type":"country","country_code":"BQ","supports_region":false,"supports_city":false},{"key":"BR","name":"Brazil","type":"country","country_code":"BR","supports_region":true,"supports_city":true},{"key":"BS","name":"The Bahamas","type":"country","country_code":"BS","supports_region":false,"supports_city":false},{"key":"BT","name":"Bhutan","type":"country","country_code":"BT","supports_region":true,"supports_city":false},{"key":"BV","name":"Bouvet Island","type":"country","country_code":"BV","supports_region":false,"supports_city":false},{"key":"BW","name":"Botswana","type":"country","country_code":"BW","supports_region":true,"supports_city":false},{"key":"BY","name":"Belarus","type":"country","country_code":"BY","supports_region":true,"supports_city":false},{"key":"BZ","name":"Belize","type":"country","country_code":"BZ","supports_region":true,"supports_city":false},{"key":"CA","name":"Canada","type":"country","country_code":"CA","supports_region":true,"supports_city":true},{"key":"CC","name":"Cocos (Keeling) Islands","type":"country","country_code":"CC","supports_region":false,"supports_city":false},{"key":"CD","name":"Democratic Republic of the Congo","type":"country","country_code":"CD","supports_region":false,"supports_city":false},{"key":"CF","name":"Central African Republic","type":"country","country_code":"CF","supports_region":false,"supports_city":false},{"key":"CG","name":"Republic of the Congo","type":"country","country_code":"CG","supports_region":true,"supports_city":false},{"key":"CH","name":"Switzerland","type":"country","country_code":"CH","supports_region":true,"supports_city":true},{"key":"CI","name":"Côte d'Ivoire","type":"country","country_code":"CI","supports_region":true,"supports_city":false},{"key":"CK","name":"Cook Islands","type":"country","country_code":"CK","supports_region":true,"supports_city":false},{"key":"CL","name":"Chile","type":"country","country_code":"CL","supports_region":true,"supports_city":true},{"key":"CM","name":"Cameroon","type":"country","country_code":"CM","supports_region":true,"supports_city":false},{"key":"CN","name":"China","type":"country","country_code":"CN","supports_region":false,"supports_city":false},{"key":"CO","name":"Colombia","type":"country","country_code":"CO","supports_region":true,"supports_city":true},{"key":"CR","name":"Costa Rica","type":"country","country_code":"CR","supports_region":true,"supports_city":true},{"key":"CV","name":"Cape Verde","type":"country","country_code":"CV","supports_region":true,"supports_city":false},{"key":"CW","name":"Curaçao","type":"country","country_code":"CW","supports_region":false,"supports_city":false},{"key":"CX","name":"Christmas Island","type":"country","country_code":"CX","supports_region":false,"supports_city":false},{"key":"CY","name":"Cyprus","type":"country","country_code":"CY","supports_region":true,"supports_city":false},{"key":"CZ","name":"Czech Republic","type":"country","country_code":"CZ","supports_region":true,"supports_city":true},{"key":"DE","name":"Germany","type":"country","country_code":"DE","supports_region":true,"supports_city":true},{"key":"DJ","name":"Djibouti","type":"country","country_code":"DJ","supports_region":true,"supports_city":false},{"key":"DK","name":"Denmark","type":"country","country_code":"DK","supports_region":true,"supports_city":true},{"key":"DM","name":"Dominica","type":"country","country_code":"DM","supports_region":true,"supports_city":false},{"key":"DO","name":"Dominican Republic","type":"country","country_code":"DO","supports_region":true,"supports_city":true},{"key":"DZ","name":"Algeria","type":"country","country_code":"DZ","supports_region":true,"supports_city":false},{"key":"EC","name":"Ecuador","type":"country","country_code":"EC","supports_region":true,"supports_city":true},{"key":"EE","name":"Estonia","type":"country","country_code":"EE","supports_region":true,"supports_city":false},{"key":"EG","name":"Egypt","type":"country","country_code":"EG","supports_region":true,"supports_city":true},{"key":"EH","name":"Western Sahara","type":"country","country_code":"EH","supports_region":false,"supports_city":false},{"key":"ER","name":"Eritrea","type":"country","country_code":"ER","supports_region":true,"supports_city":false},{"key":"ES","name":"Spain","type":"country","country_code":"ES","supports_region":true,"supports_city":true},{"key":"ET","name":"Ethiopia","type":"country","country_code":"ET","supports_region":true,"supports_city":false},{"key":"FI","name":"Finland","type":"country","country_code":"FI","supports_region":true,"supports_city":true},{"key":"FJ","name":"Fiji","type":"country","country_code":"FJ","supports_region":true,"supports_city":false},{"key":"FK","name":"Falkland Islands","type":"country","country_code":"FK","supports_region":false,"supports_city":false},{"key":"FM","name":"Federated States of Micronesia","type":"country","country_code":"FM","supports_region":false,"supports_city":false},{"key":"FO","name":"Faroe Islands","type":"country","country_code":"FO","supports_region":true,"supports_city":false},{"key":"FR","name":"France","type":"country","country_code":"FR","supports_region":true,"supports_city":true},{"key":"GA","name":"Gabon","type":"country","country_code":"GA","supports_region":true,"supports_city":false},{"key":"GB","name":"United Kingdom","type":"country","country_code":"GB","supports_region":true,"supports_city":true},{"key":"GD","name":"Grenada","type":"country","country_code":"GD","supports_region":true,"supports_city":false},{"key":"GE","name":"Georgia","type":"country","country_code":"GE","supports_region":true,"supports_city":false},{"key":"GF","name":"French Guiana","type":"country","country_code":"GF","supports_region":false,"supports_city":false},{"key":"GG","name":"Guernsey","type":"country","country_code":"GG","supports_region":true,"supports_city":false},{"key":"GH","name":"Ghana","type":"country","country_code":"GH","supports_region":true,"supports_city":false},{"key":"GI","name":"Gibraltar","type":"country","country_code":"GI","supports_region":false,"supports_city":false},{"key":"GL","name":"Greenland","type":"country","country_code":"GL","supports_region":true,"supports_city":false},{"key":"GM","name":"The Gambia","type":"country","country_code":"GM","supports_region":true,"supports_city":false},{"key":"GN","name":"Guinea","type":"country","country_code":"GN","supports_region":true,"supports_city":false},{"key":"GP","name":"Guadeloupe","type":"country","country_code":"GP","supports_region":false,"supports_city":false},{"key":"GQ","name":"Equatorial Guinea","type":"country","country_code":"GQ","supports_region":true,"supports_city":false},{"key":"GR","name":"Greece","type":"country","country_code":"GR","supports_region":true,"supports_city":true},{"key":"GS","name":"South Georgia and the South Sandwich Islands","type":"country","country_code":"GS","supports_region":false,"supports_city":false},{"key":"GT","name":"Guatemala","type":"country","country_code":"GT","supports_region":true,"supports_city":true},{"key":"GU","name":"Guam","type":"country","country_code":"GU","supports_region":false,"supports_city":false},{"key":"GW","name":"Guinea-Bissau","type":"country","country_code":"GW","supports_region":true,"supports_city":false},{"key":"GY","name":"Guyana","type":"country","country_code":"GY","supports_region":true,"supports_city":false},{"key":"HK","name":"Hong Kong","type":"country","country_code":"HK","supports_region":true,"supports_city":false},{"key":"HM","name":"Heard Island and McDonald Islands","type":"country","country_code":"HM","supports_region":false,"supports_city":false},{"key":"HN","name":"Honduras","type":"country","country_code":"HN","supports_region":true,"supports_city":true},{"key":"HR","name":"Croatia","type":"country","country_code":"HR","supports_region":true,"supports_city":false},{"key":"HT","name":"Haiti","type":"country","country_code":"HT","supports_region":true,"supports_city":false},{"key":"HU","name":"Hungary","type":"country","country_code":"HU","supports_region":true,"supports_city":true},{"key":"ID","name":"Indonesia","type":"country","country_code":"ID","supports_region":true,"supports_city":true},{"key":"IE","name":"Ireland","type":"country","country_code":"IE","supports_region":true,"supports_city":true},{"key":"IL","name":"Israel","type":"country","country_code":"IL","supports_region":true,"supports_city":true},{"key":"IM","name":"Isle Of Man","type":"country","country_code":"IM","supports_region":false,"supports_city":false},{"key":"IN","name":"India","type":"country","country_code":"IN","supports_region":true,"supports_city":true},{"key":"IO","name":"British Indian Ocean Territory","type":"country","country_code":"IO","supports_region":false,"supports_city":false},{"key":"IQ","name":"Iraq","type":"country","country_code":"IQ","supports_region":true,"supports_city":false},{"key":"IS","name":"Iceland","type":"country","country_code":"IS","supports_region":true,"supports_city":false},{"key":"IT","name":"Italy","type":"country","country_code":"IT","supports_region":true,"supports_city":true},{"key":"JE","name":"Jersey","type":"country","country_code":"JE","supports_region":true,"supports_city":false},{"key":"JM","name":"Jamaica","type":"country","country_code":"JM","supports_region":true,"supports_city":false},{"key":"JO","name":"Jordan","type":"country","country_code":"JO","supports_region":true,"supports_city":false},{"key":"JP","name":"Japan","type":"country","country_code":"JP","supports_region":true,"supports_city":true},{"key":"KE","name":"Kenya","type":"country","country_code":"KE","supports_region":true,"supports_city":false},{"key":"KG","name":"Kyrgyzstan","type":"country","country_code":"KG","supports_region":true,"supports_city":false},{"key":"KH","name":"Cambodia","type":"country","country_code":"KH","supports_region":true,"supports_city":false},{"key":"KI","name":"Kiribati","type":"country","country_code":"KI","supports_region":false,"supports_city":false},{"key":"KM","name":"Comoros","type":"country","country_code":"KM","supports_region":true,"supports_city":false},{"key":"KN","name":"Saint Kitts and Nevis","type":"country","country_code":"KN","supports_region":false,"supports_city":false},{"key":"KP","name":"North Korea","type":"country","country_code":"KP","supports_region":false,"supports_city":false},{"key":"KR","name":"South Korea","type":"country","country_code":"KR","supports_region":true,"supports_city":true},{"key":"KW","name":"Kuwait","type":"country","country_code":"KW","supports_region":true,"supports_city":false},{"key":"KY","name":"Cayman Islands","type":"country","country_code":"KY","supports_region":true,"supports_city":false},{"key":"KZ","name":"Kazakhstan","type":"country","country_code":"KZ","supports_region":true,"supports_city":false},{"key":"LA","name":"Laos","type":"country","country_code":"LA","supports_region":true,"supports_city":false},{"key":"LB","name":"Lebanon","type":"country","country_code":"LB","supports_region":true,"supports_city":false},{"key":"LC","name":"St. Lucia","type":"country","country_code":"LC","supports_region":false,"supports_city":false},{"key":"LI","name":"Liechtenstein","type":"country","country_code":"LI","supports_region":true,"supports_city":false},{"key":"LK","name":"Sri Lanka","type":"country","country_code":"LK","supports_region":true,"supports_city":false},{"key":"LR","name":"Liberia","type":"country","country_code":"LR","supports_region":true,"supports_city":false},{"key":"LS","name":"Lesotho","type":"country","country_code":"LS","supports_region":true,"supports_city":false},{"key":"LT","name":"Lithuania","type":"country","country_code":"LT","supports_region":true,"supports_city":false},{"key":"LU","name":"Luxembourg","type":"country","country_code":"LU","supports_region":true,"supports_city":false},{"key":"LV","name":"Latvia","type":"country","country_code":"LV","supports_region":true,"supports_city":false},{"key":"LY","name":"Libya","type":"country","country_code":"LY","supports_region":true,"supports_city":false},{"key":"MA","name":"Morocco","type":"country","country_code":"MA","supports_region":true,"supports_city":false},{"key":"MC","name":"Monaco","type":"country","country_code":"MC","supports_region":true,"supports_city":false},{"key":"MD","name":"Moldova","type":"country","country_code":"MD","supports_region":true,"supports_city":false},{"key":"ME","name":"Montenegro","type":"country","country_code":"ME","supports_region":true,"supports_city":false},{"key":"MF","name":"Saint Martin","type":"country","country_code":"MF","supports_region":false,"supports_city":false},{"key":"MG","name":"Madagascar","type":"country","country_code":"MG","supports_region":true,"supports_city":false},{"key":"MH","name":"Marshall Islands","type":"country","country_code":"MH","supports_region":true,"supports_city":false},{"key":"MK","name":"Macedonia","type":"country","country_code":"MK","supports_region":true,"supports_city":false},{"key":"ML","name":"Mali","type":"country","country_code":"ML","supports_region":true,"supports_city":false},{"key":"MM","name":"Myanmar","type":"country","country_code":"MM","supports_region":false,"supports_city":false},{"key":"MN","name":"Mongolia","type":"country","country_code":"MN","supports_region":true,"supports_city":false},{"key":"MO","name":"Macau","type":"country","country_code":"MO","supports_region":false,"supports_city":false},{"key":"MP","name":"Northern Mariana Islands","type":"country","country_code":"MP","supports_region":false,"supports_city":false},{"key":"MQ","name":"Martinique","type":"country","country_code":"MQ","supports_region":false,"supports_city":false},{"key":"MR","name":"Mauritania","type":"country","country_code":"MR","supports_region":true,"supports_city":false},{"key":"MS","name":"Montserrat","type":"country","country_code":"MS","supports_region":false,"supports_city":false},{"key":"MT","name":"Malta","type":"country","country_code":"MT","supports_region":true,"supports_city":false},{"key":"MU","name":"Mauritius","type":"country","country_code":"MU","supports_region":true,"supports_city":false},{"key":"MV","name":"Maldives","type":"country","country_code":"MV","supports_region":true,"supports_city":false},{"key":"MW","name":"Malawi","type":"country","country_code":"MW","supports_region":true,"supports_city":false},{"key":"MX","name":"Mexico","type":"country","country_code":"MX","supports_region":true,"supports_city":true},{"key":"MY","name":"Malaysia","type":"country","country_code":"MY","supports_region":true,"supports_city":true},{"key":"MZ","name":"Mozambique","type":"country","country_code":"MZ","supports_region":true,"supports_city":false},{"key":"NA","name":"Namibia","type":"country","country_code":"NA","supports_region":true,"supports_city":false},{"key":"NC","name":"New Caledonia","type":"country","country_code":"NC","supports_region":true,"supports_city":false},{"key":"NE","name":"Niger","type":"country","country_code":"NE","supports_region":true,"supports_city":false},{"key":"NF","name":"Norfolk Island","type":"country","country_code":"NF","supports_region":false,"supports_city":false},{"key":"NG","name":"Nigeria","type":"country","country_code":"NG","supports_region":true,"supports_city":true},{"key":"NI","name":"Nicaragua","type":"country","country_code":"NI","supports_region":true,"supports_city":true},{"key":"NL","name":"Netherlands","type":"country","country_code":"NL","supports_region":true,"supports_city":true},{"key":"NO","name":"Norway","type":"country","country_code":"NO","supports_region":true,"supports_city":true},{"key":"NP","name":"Nepal","type":"country","country_code":"NP","supports_region":true,"supports_city":false},{"key":"NR","name":"Nauru","type":"country","country_code":"NR","supports_region":true,"supports_city":false},{"key":"NU","name":"Niue","type":"country","country_code":"NU","supports_region":false,"supports_city":false},{"key":"NZ","name":"New Zealand","type":"country","country_code":"NZ","supports_region":true,"supports_city":true},{"key":"OM","name":"Oman","type":"country","country_code":"OM","supports_region":true,"supports_city":false},{"key":"PA","name":"Panama","type":"country","country_code":"PA","supports_region":true,"supports_city":true},{"key":"PE","name":"Peru","type":"country","country_code":"PE","supports_region":true,"supports_city":true},{"key":"PF","name":"French Polynesia","type":"country","country_code":"PF","supports_region":true,"supports_city":false},{"key":"PG","name":"Papua New Guinea","type":"country","country_code":"PG","supports_region":true,"supports_city":false},{"key":"PH","name":"Philippines","type":"country","country_code":"PH","supports_region":true,"supports_city":true},{"key":"PK","name":"Pakistan","type":"country","country_code":"PK","supports_region":true,"supports_city":false},{"key":"PL","name":"Poland","type":"country","country_code":"PL","supports_region":true,"supports_city":true},{"key":"PM","name":"Saint Pierre and Miquelon","type":"country","country_code":"PM","supports_region":false,"supports_city":false},{"key":"PN","name":"Pitcairn","type":"country","country_code":"PN","supports_region":false,"supports_city":false},{"key":"PR","name":"Puerto Rico","type":"country","country_code":"PR","supports_region":true,"supports_city":true},{"key":"PS","name":"Palestine","type":"country","country_code":"PS","supports_region":false,"supports_city":false},{"key":"PT","name":"Portugal","type":"country","country_code":"PT","supports_region":true,"supports_city":true},{"key":"PW","name":"Palau","type":"country","country_code":"PW","supports_region":false,"supports_city":false},{"key":"PY","name":"Paraguay","type":"country","country_code":"PY","supports_region":true,"supports_city":true},{"key":"QA","name":"Qatar","type":"country","country_code":"QA","supports_region":true,"supports_city":false},{"key":"RE","name":"Réunion","type":"country","country_code":"RE","supports_region":false,"supports_city":false},{"key":"RO","name":"Romania","type":"country","country_code":"RO","supports_region":true,"supports_city":true},{"key":"RS","name":"Serbia","type":"country","country_code":"RS","supports_region":false,"supports_city":true},{"key":"RU","name":"Russia","type":"country","country_code":"RU","supports_region":true,"supports_city":true},{"key":"RW","name":"Rwanda","type":"country","country_code":"RW","supports_region":true,"supports_city":false},{"key":"SA","name":"Saudi Arabia","type":"country","country_code":"SA","supports_region":true,"supports_city":true},{"key":"SB","name":"Solomon Islands","type":"country","country_code":"SB","supports_region":true,"supports_city":false},{"key":"SC","name":"Seychelles","type":"country","country_code":"SC","supports_region":true,"supports_city":false},{"key":"SE","name":"Sweden","type":"country","country_code":"SE","supports_region":true,"supports_city":true},{"key":"SG","name":"Singapore","type":"country","country_code":"SG","supports_region":true,"supports_city":false},{"key":"SH","name":"Saint Helena","type":"country","country_code":"SH","supports_region":true,"supports_city":false},{"key":"SI","name":"Slovenia","type":"country","country_code":"SI","supports_region":true,"supports_city":false},{"key":"SJ","name":"Svalbard and Jan Mayen","type":"country","country_code":"SJ","supports_region":false,"supports_city":false},{"key":"SK","name":"Slovakia","type":"country","country_code":"SK","supports_region":true,"supports_city":false},{"key":"SL","name":"Sierra Leone","type":"country","country_code":"SL","supports_region":true,"supports_city":false},{"key":"SM","name":"San Marino","type":"country","country_code":"SM","supports_region":true,"supports_city":false},{"key":"SN","name":"Senegal","type":"country","country_code":"SN","supports_region":true,"supports_city":false},{"key":"SO","name":"Somalia","type":"country","country_code":"SO","supports_region":true,"supports_city":false},{"key":"SR","name":"Suriname","type":"country","country_code":"SR","supports_region":true,"supports_city":false},{"key":"SS","name":"South Sudan","type":"country","country_code":"SS","supports_region":true,"supports_city":false},{"key":"ST","name":"Sao Tome and Principe","type":"country","country_code":"ST","supports_region":true,"supports_city":false},{"key":"SV","name":"El Salvador","type":"country","country_code":"SV","supports_region":true,"supports_city":true},{"key":"SX","name":"Sint Maarten","type":"country","country_code":"SX","supports_region":false,"supports_city":false},{"key":"SZ","name":"Swaziland","type":"country","country_code":"SZ","supports_region":true,"supports_city":false},{"key":"TC","name":"Turks and Caicos Islands","type":"country","country_code":"TC","supports_region":false,"supports_city":false},{"key":"TD","name":"Chad","type":"country","country_code":"TD","supports_region":true,"supports_city":false},{"key":"TF","name":"French Southern Territories","type":"country","country_code":"TF","supports_region":false,"supports_city":false},{"key":"TG","name":"Togo","type":"country","country_code":"TG","supports_region":true,"supports_city":false},{"key":"TH","name":"Thailand","type":"country","country_code":"TH","supports_region":true,"supports_city":true},{"key":"TJ","name":"Tajikistan","type":"country","country_code":"TJ","supports_region":true,"supports_city":false},{"key":"TK","name":"Tokelau","type":"country","country_code":"TK","supports_region":true,"supports_city":false},{"key":"TL","name":"Timor-Leste","type":"country","country_code":"TL","supports_region":false,"supports_city":false},{"key":"TM","name":"Turkmenistan","type":"country","country_code":"TM","supports_region":true,"supports_city":false},{"key":"TN","name":"Tunisia","type":"country","country_code":"TN","supports_region":true,"supports_city":false},{"key":"TO","name":"Tonga","type":"country","country_code":"TO","supports_region":true,"supports_city":false},{"key":"TR","name":"Turkey","type":"country","country_code":"TR","supports_region":true,"supports_city":true},{"key":"TT","name":"Trinidad and Tobago","type":"country","country_code":"TT","supports_region":true,"supports_city":false},{"key":"TV","name":"Tuvalu","type":"country","country_code":"TV","supports_region":true,"supports_city":false},{"key":"TW","name":"Taiwan","type":"country","country_code":"TW","supports_region":true,"supports_city":true},{"key":"TZ","name":"Tanzania","type":"country","country_code":"TZ","supports_region":false,"supports_city":false},{"key":"UA","name":"Ukraine","type":"country","country_code":"UA","supports_region":true,"supports_city":true},{"key":"UG","name":"Uganda","type":"country","country_code":"UG","supports_region":true,"supports_city":false},{"key":"UM","name":"United States Minor Outlying Islands","type":"country","country_code":"UM","supports_region":false,"supports_city":false},{"key":"US","name":"United States","type":"country","country_code":"US","supports_region":true,"supports_city":true},{"key":"UY","name":"Uruguay","type":"country","country_code":"UY","supports_region":true,"supports_city":true},{"key":"UZ","name":"Uzbekistan","type":"country","country_code":"UZ","supports_region":true,"supports_city":false},{"key":"VA","name":"Vatican City","type":"country","country_code":"VA","supports_region":false,"supports_city":false},{"key":"VC","name":"Saint Vincent and the Grenadines","type":"country","country_code":"VC","supports_region":false,"supports_city":false},{"key":"VE","name":"Venezuela","type":"country","country_code":"VE","supports_region":true,"supports_city":true},{"key":"VG","name":"British Virgin Islands","type":"country","country_code":"VG","supports_region":false,"supports_city":false},{"key":"VI","name":"US Virgin Islands","type":"country","country_code":"VI","supports_region":false,"supports_city":false},{"key":"VN","name":"Vietnam","type":"country","country_code":"VN","supports_region":true,"supports_city":true},{"key":"VU","name":"Vanuatu","type":"country","country_code":"VU","supports_region":true,"supports_city":false},{"key":"WF","name":"Wallis and Futuna","type":"country","country_code":"WF","supports_region":false,"supports_city":false},{"key":"WS","name":"Samoa","type":"country","country_code":"WS","supports_region":false,"supports_city":false},{"key":"XK","name":"Kosovo","type":"country","country_code":"XK","supports_region":false,"supports_city":false},{"key":"YE","name":"Yemen","type":"country","country_code":"YE","supports_region":true,"supports_city":false},{"key":"YT","name":"Mayotte","type":"country","country_code":"YT","supports_region":false,"supports_city":false},{"key":"ZA","name":"South Africa","type":"country","country_code":"ZA","supports_region":true,"supports_city":true},{"key":"ZM","name":"Zambia","type":"country","country_code":"ZM","supports_region":true,"supports_city":false},{"key":"ZW","name":"Zimbabwe","type":"country","country_code":"ZW","supports_region":true,"supports_city":false}];
    var languageList = ["", "Albanian","Amharic","Arabic","Armenian","Azerbaijani","Bengali","Bosnian","Bulgarian","Burmese","Catalan","Chinese","Croatian","Czech","Danish","Dutch","English","Estonian","Finnish","Filipino","French","German","Georgian","Greek","Hungarian", "Hindi","Hebrew","Icelandic","Indonesian","Italian","Japanese","Korean","Kyrgyz","Lao","Latvian","Lithuanian","Luxembourgish","Macedonian","Malagasy","Malay","Mongolian","Nepali","Pashto","Polish","Portuguese","Romanian","Russian","Samoan","Serbian","Sinhala","Slovak","Slovenian", "Somali","Spanish","Swahili","Swedish","Tajik","Thai","Norwegian","Traditional","Turkish","Ukrainian","Urdu","Uzbek","Vietnamese"];

    var appList = [];
    var admobRegionCodes = {"All":"","Sint Maarten":"SX","Cuba":"CU","Curacao":"CW","Iran":"IR","Afghanistan":"AF","Albania":"AL","Antarctica":"AQ","Algeria":"DZ","American Samoa":"AS","Andorra":"AD","Angola":"AO","Antigua and Barbuda":"AG","Azerbaijan":"AZ","Argentina":"AR","Australia":"AU","Austria":"AT","The Bahamas":"BS","Bahrain":"BH","Bangladesh":"BD","Armenia":"AM","Barbados":"BB","Belgium":"BE","Bermuda":"BM","Bhutan":"BT","Bolivia":"BO","Bosnia and Herzegovina":"BA","Botswana":"BW","Bouvet Island":"BV","Brazil":"BR","Belize":"BZ","British Indian Ocean Territory":"IO","Solomon Islands":"SB","British Virgin Islands":"VG","Brunei":"BN","Bulgaria":"BG","Myanmar (Burma)":"MM","Burundi":"BI","Belarus":"BY","Cambodia":"KH","Cameroon":"CM","Canada":"CA","Cape Verde":"CV","Cayman Islands":"KY","Central African Republic":"CF","Sri Lanka":"LK","Chad":"TD","Chile":"CL","China":"CN","Taiwan":"TW","Christmas Island":"CX","Cocos (Keeling) Islands":"CC","Colombia":"CO","Comoros":"KM","Mayotte":"YT","Republic of the Congo":"CG","Democratic Republic of the Congo":"CD","Cook Islands":"CK","Costa Rica":"CR","Croatia":"HR","Cyprus":"CY","Czechia":"CZ","Benin":"BJ","Denmark":"DK","Dominica":"DM","Dominican Republic":"DO","Ecuador":"EC","El Salvador":"SV","Equatorial Guinea":"GQ","Ethiopia":"ET","Eritrea":"ER","Estonia":"EE","Faroe Islands":"FO","Falkland Islands (Islas Malvinas)":"FK","South Georgia and the South Sandwich Islands":"GS","Fiji":"FJ","Finland":"FI","France":"FR","French Guiana":"GF","French Polynesia":"PF","French Southern and Antarctic Lands":"TF","Djibouti":"DJ","Gabon":"GA","Georgia":"GE","The Gambia":"GM","Palestine":"PS","Germany":"DE","Ghana":"GH","Gibraltar":"GI","Kiribati":"KI","Greece":"GR","Greenland":"GL","Grenada":"GD","Guadeloupe":"GP","Guam":"GU","Guatemala":"GT","Guinea":"GN","Guyana":"GY","Haiti":"HT","Heard Island and McDonald Islands":"HM","Vatican City":"VA","Honduras":"HN","Hong Kong":"HK","Hungary":"HU","Iceland":"IS","India":"IN","Indonesia":"ID","Iraq":"IQ","Ireland":"IE","Israel":"IL","Italy":"IT","Cote d'Ivoire":"CI","Jamaica":"JM","Japan":"JP","Kazakhstan":"KZ","Jordan":"JO","Kenya":"KE","South Korea":"KR","Kuwait":"KW","Kyrgyzstan":"KG","Laos":"LA","Lebanon":"LB","Lesotho":"LS","Latvia":"LV","Liberia":"LR","Libya":"LY","Liechtenstein":"LI","Lithuania":"LT","Luxembourg":"LU","Macau":"MO","Madagascar":"MG","Malawi":"MW","Malaysia":"MY","Maldives":"MV","Mali":"ML","Malta":"MT","Martinique":"MQ","Mauritania":"MR","Mauritius":"MU","Mexico":"MX","Monaco":"MC","Mongolia":"MN","Moldova":"MD","Montenegro":"ME","Montserrat":"MS","Morocco":"MA","Mozambique":"MZ","Oman":"OM","Namibia":"NA","Nauru":"NR","Nepal":"NP","Netherlands":"NL","Netherlands Antilles":"BQ","Aruba":"AW","New Caledonia":"NC","Vanuatu":"VU","New Zealand":"NZ","Nicaragua":"NI","Niger":"NE","Nigeria":"NG","Niue":"NU","Norfolk Island":"NF","Norway":"NO","Northern Mariana Islands":"MP","United States Minor Outlying Islands":"UM","Federated States of Micronesia":"FM","Marshall Islands":"MH","Palau":"PW","Pakistan":"PK","Panama":"PA","Papua New Guinea":"PG","Paraguay":"PY","Peru":"PE","Philippines":"PH","Pitcairn Islands":"PN","Poland":"PL","Portugal":"PT","Guinea-Bissau":"GW","Timor-Leste":"TL","Puerto Rico":"PR","Qatar":"QA","Reunion":"RE","Romania":"RO","Russia":"RU","Rwanda":"RW","Saint Helena, Ascension and Tristan da Cunha":"SH","Saint Kitts and Nevis":"KN","Anguilla":"AI","Saint Lucia":"LC","Saint Pierre and Miquelon":"PM","Saint Vincent and the Grenadines":"VC","San Marino":"SM","Sao Tome and Principe":"ST","Saudi Arabia":"SA","Senegal":"SN","Serbia":"RS","Seychelles":"SC","Sierra Leone":"SL","Singapore":"SG","Slovakia":"SK","Vietnam":"VN","Slovenia":"SI","Somalia":"SO","South Africa":"ZA","Zimbabwe":"ZW","Spain":"ES","Western Sahara":"EH","Suriname":"SR","Svalbard and Jan Mayen":"SJ","Swaziland":"SZ","Sweden":"SE","Switzerland":"CH","Tajikistan":"TJ","Thailand":"TH","Togo":"TG","Tokelau":"TK","Tonga":"TO","Trinidad and Tobago":"TT","United Arab Emirates":"AE","Tunisia":"TN","Turkey":"TR","Turkmenistan":"TM","Turks and Caicos Islands":"TC","Tuvalu":"TV","Uganda":"UG","Ukraine":"UA","Macedonia (FYROM)":"MK","Egypt":"EG","United Kingdom":"GB","Guernsey":"GG","Jersey":"JE","Tanzania":"TZ","United States":"US","U.S. Virgin Islands":"VI","Burkina Faso":"BF","Uruguay":"UY","Uzbekistan":"UZ","Venezuela":"VE","Wallis and Futuna":"WF","Samoa":"WS","Yemen":"YE","Zambia":"ZM","Kosovo":"XK"};

    function init() {
        $('.select2').select2();

        $('.btn-more').click(function() {
            var id = $(this).attr('id');
            var targetId = '';
            if (id == 'btnSelectRegionMore') {
                targetId = '#selectRegion';
            } else if (id == 'btnSelectRegionUnselectedMore') {
                targetId = '#selectRegionUnselected';
            } else if (id == 'btnSelectRegionUnselectedAdmobMore') {
                targetId = '#selectRegionUnselectedAdmob';
            } else if (id == 'btnSelectRegionAdmobMore') {
                targetId = '#selectRegionAdmob';
            }
            $('#moreCountryDlg').modal("show");
            $('#moreCountryDlg .btn-primary').off('click');
            $('#moreCountryDlg .btn-primary').click(function() {
                console.log(id);
                var data = $('#textareaCountry').val();
                var countryList = data.split('\n');
                var countryNames = [];
                countryList.forEach(function(one) {
                    for (var i = 0; i < regionList.length; i++) {
                        one = one.trim();
                        if (regionList[i].name.toLocaleLowerCase() == one.toLocaleLowerCase()) {
                            if (targetId == '#selectRegionUnselectedAdmob' || targetId == '#selectRegionAdmob') {
                                countryNames.push(regionList[i].country_code);
                            } else {
                                countryNames.push(regionList[i].name);
                            }
                            break;
                        }
                    }
                });

                if (targetId != '') {
                    $(targetId).val(countryNames);
                    $(targetId).trigger('change');
                }
                $('#moreCountryDlg').modal("hide");
            });
        });



        languageList.forEach(function (one) {
            $('#selectLanguage').append($("<option>" + one + "</option>"));
            $('#selectLanguageAdmob').append($("<option>" + one + "</option>"));
        });


        for (var k in admobRegionCodes) {
            var key, value;
            key = k;
            value = admobRegionCodes[k];
            $('#selectRegionAdmob').append($("<option value='" + value + "'>" + key + "</option>"));
            $('#selectRegionUnselectedAdmob').append($("<option value='" + value + "'>" + key + "</option>"));
        }

        $.post('system/fb_app_id_rel/query', {
            word: '',
        }, function(data) {
            if (data && data.ret == 1) {
                appList = data.data;
                appList.forEach(function(one) {
                    $('#selectApp').append($("<option>" + one.tag_name + "</option>"));
                    $('#selectAppAdmob').append($("<option>" + one.tag_name + "</option>"));
                })
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');

        $.post('adaccount_admob/query',{word:''}, function(data) {
            if (data && data.ret == 1) {
                var accountList = data.data;
                accountList.forEach(function(one) {
                    $('#selectAccountAdmob').append($("<option value='" + one.account_id + "'>" + one.short_name + "</option>"));
                })
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');

        $.post('adaccount/query',{word:''}, function(data) {
            if (data && data.ret == 1) {
                var accountList = data.data;
                accountList.forEach(function(one) {
                    $('#selectAccount').append($("<option value='" + one.account_id + "'>" + one.short_name + "</option>"));
                })
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');

        $('#checkAdmob').click(function () {
            if ($('#checkAdmob').prop('checked')) {
                $('#formFacebook').hide();
                $('#formAdmob').show();
            }
        });
        $('#checkFacebook').click(function () {
            if ($('#checkFacebook').prop('checked')) {
                $('#formFacebook').show();
                $('#formAdmob').hide();
            }
        });

        $('#formAdmob').hide();


    }

    init();

    getBLen = function(str) {
        if (str == null) return 0;
        if (typeof str != "string"){
            str += "";
        }
        return str.replace(/[^\x00-\xff]/g,"01").length;
    }

    $('#btnInsertAdmob').click(function() {
        var appName = $('#selectAppAdmob').val();
        var language = $('#selectLanguageAdmob').val();
        var message11 = $('#inputMessageAdmob11').val();
        var message12 = $('#inputMessageAdmob12').val();
        var message13 = $('#inputMessageAdmob13').val();
        var message14 = $('#inputMessageAdmob14').val();
        var message21 = $('#inputMessageAdmob21').val();
        var message22 = $('#inputMessageAdmob22').val();
        var message23 = $('#inputMessageAdmob23').val();
        var message24 = $('#inputMessageAdmob24').val();
        var message31 = $('#inputMessageAdmob31').val();
        var message32 = $('#inputMessageAdmob32').val();
        var message33 = $('#inputMessageAdmob33').val();
        var message34 = $('#inputMessageAdmob34').val();
        var message41 = $('#inputMessageAdmob41').val();
        var message42 = $('#inputMessageAdmob42').val();
        var message43 = $('#inputMessageAdmob43').val();
        var message44 = $('#inputMessageAdmob44').val();

        $.post("advert_admob/save_advert_admob", {
            appName: appName,
            language: language,
            message11: message11,
            message12: message12,
            message13: message13,
            message14: message14,
            message21: message21,
            message22: message22,
            message23: message23,
            message24: message24,
            message31: message31,
            message32: message32,
            message33: message33,
            message34: message34,
            message41: message41,
            message42: message42,
            message43: message43,
            message44: message44
        }, function (data) {
            if (data && data.ret == 1) {
                if(data.existData == "true"){
                    admanager.showCommonDlg("提示", "更新记录成功");
                }else{
                    admanager.showCommonDlg("提示", "添加记录成功");
                }

            } else {
                admanager.showCommonDlg("提示", data.message);
            }
        }, "json");
        return false;
    });


    $('#btnInsert').click(function () {
        var appName = $('#selectApp').val();
        var language = $('#selectLanguage').val();
        var title11 = $('#inputTitle11').val();
        var message11 = $('#inputMessage11').val();
        var title22 = $('#inputTitle22').val();
        var message22 = $('#inputMessage22').val();
        var title33 = $('#inputTitle33').val();
        var message33 = $('#inputMessage33').val();
        var title44 = $('#inputTitle44').val();
        var message44 = $('#inputMessage44').val();
        $.post("advert/save_advert_facebook", {
            appName: appName,
            language: language,
            title11: title11,
            message11: message11,
            title22: title22,
            message22: message22,
            title33: title33,
            message33: message33,
            title44: title44,
            message44: message44
        }, function (data) {
            if (data && data.ret == 1) {
                if(data.existData == "true"){
                    admanager.showCommonDlg("提示", "更新记录成功");
                }else{
                    admanager.showCommonDlg("提示", "添加记录成功");
                }
            } else {
                admanager.showCommonDlg("提示", data.message);
            }
        }, "json");
        return false;
    });

    $('#selectApp,#selectLanguage').change(function () {
        $('#inputTitle11').val("");
        $('#inputMessage11').val("");
        $('#inputTitle22').val("");
        $('#inputMessage22').val("");
        $('#inputTitle33').val("");
        $('#inputMessage33').val("");
        $('#inputTitle44').val("");
        $('#inputMessage44').val("");
        var appName = $("#selectApp").val();
        var language = $("#selectLanguage").val();
        if(language != null &&  language.length > 0){
            $.post("advert/query_before_insertion", {
                appName: appName,
                language: language
            }, function (data) {
                if (data && data.ret == 1) {
                    var arr = data.array;
                    for(var i =0;i< arr.length;i++){
                        var one = arr[i];
                        if(one['group_id'] == 1){
                            $('#inputTitle11').val(one['title']);
                            $('#inputMessage11').val(one['message']);
                        }else if(one['group_id'] == 2){
                            $('#inputTitle22').val(one['title']);
                            $('#inputMessage22').val(one['message']);
                        }else if(one['group_id'] == 3){
                            $('#inputTitle33').val(one['title']);
                            $('#inputMessage33').val(one['message']);
                        }else if(one['group_id'] == 4){
                            $('#inputTitle44').val(one['title']);
                            $('#inputMessage44').val(one['message']);
                        }

                    }
                } else {
                    $('#inputTitle11').val("");
                    $('#inputMessage11').val("");
                    $('#inputTitle22').val("");
                    $('#inputMessage22').val("");
                    $('#inputTitle33').val("");
                    $('#inputMessage33').val("");
                    $('#inputTitle44').val("");
                    $('#inputMessage44').val("");
                }
            }, "json");
        }else{
            $('#inputTitle11').val("");
            $('#inputMessage11').val("");
            $('#inputTitle22').val("");
            $('#inputMessage22').val("");
            $('#inputTitle33').val("");
            $('#inputMessage33').val("");
            $('#inputTitle44').val("");
            $('#inputMessage44').val("");
        }
        return false;
    });

    $('#selectAppAdmob,#selectLanguageAdmob').change(function () {
        $('#inputMessageAdmob11').val("");
        $('#inputMessageAdmob12').val("");
        $('#inputMessageAdmob13').val("");
        $('#inputMessageAdmob14').val("");
        $('#inputMessageAdmob21').val("");
        $('#inputMessageAdmob22').val("");
        $('#inputMessageAdmob23').val("");
        $('#inputMessageAdmob24').val("");
        $('#inputMessageAdmob31').val("");
        $('#inputMessageAdmob32').val("");
        $('#inputMessageAdmob33').val("");
        $('#inputMessageAdmob34').val("");
        $('#inputMessageAdmob41').val("");
        $('#inputMessageAdmob42').val("");
        $('#inputMessageAdmob43').val("");
        $('#inputMessageAdmob44').val("");
        var appNameAdmob = $("#selectAppAdmob").val();
        var languageAdmob = $("#selectLanguageAdmob").val();
        if(languageAdmob != null &&  languageAdmob.length > 0){
            $.post("advert_admob/query_before_admob_insert", {
                appNameAdmob: appNameAdmob,
                languageAdmob: languageAdmob
            }, function (data) {
                if (data && data.ret == 1) {
                    var arr = data.array;
                    for(var i =0;i< arr.length;i++){
                        var one = arr[i];
                        if(one['group_id'] == 1){
                            $('#inputMessageAdmob11').val(one['message1']);
                            $('#inputMessageAdmob12').val(one['message2']);
                            $('#inputMessageAdmob13').val(one['message3']);
                            $('#inputMessageAdmob14').val(one['message4']);
                        } else if(one['group_id'] == 2){
                            $('#inputMessageAdmob21').val(one['message1']);
                            $('#inputMessageAdmob22').val(one['message2']);
                            $('#inputMessageAdmob23').val(one['message3']);
                            $('#inputMessageAdmob24').val(one['message4']);
                        }else if(one['group_id'] == 3){
                            $('#inputMessageAdmob31').val(one['message1']);
                            $('#inputMessageAdmob32').val(one['message2']);
                            $('#inputMessageAdmob33').val(one['message3']);
                            $('#inputMessageAdmob34').val(one['message4']);
                        }else if(one['group_id'] == 4){
                            $('#inputMessageAdmob41').val(one['message1']);
                            $('#inputMessageAdmob42').val(one['message2']);
                            $('#inputMessageAdmob43').val(one['message3']);
                            $('#inputMessageAdmob44').val(one['message4']);
                        }
                    }

                } else {
                    $('#inputMessageAdmob11').val("");
                    $('#inputMessageAdmob12').val("");
                    $('#inputMessageAdmob13').val("");
                    $('#inputMessageAdmob14').val("");
                    $('#inputMessageAdmob21').val("");
                    $('#inputMessageAdmob22').val("");
                    $('#inputMessageAdmob23').val("");
                    $('#inputMessageAdmob24').val("");
                    $('#inputMessageAdmob31').val("");
                    $('#inputMessageAdmob32').val("");
                    $('#inputMessageAdmob33').val("");
                    $('#inputMessageAdmob34').val("");
                    $('#inputMessageAdmob41').val("");
                    $('#inputMessageAdmob42').val("");
                    $('#inputMessageAdmob43').val("");
                    $('#inputMessageAdmob44').val("");
                }
            }, "json");
        }else{
            $('#inputMessageAdmob11').val("");
            $('#inputMessageAdmob12').val("");
            $('#inputMessageAdmob13').val("");
            $('#inputMessageAdmob14').val("");
            $('#inputMessageAdmob21').val("");
            $('#inputMessageAdmob22').val("");
            $('#inputMessageAdmob23').val("");
            $('#inputMessageAdmob24').val("");
            $('#inputMessageAdmob31').val("");
            $('#inputMessageAdmob32').val("");
            $('#inputMessageAdmob33').val("");
            $('#inputMessageAdmob34').val("");
            $('#inputMessageAdmob41').val("");
            $('#inputMessageAdmob42').val("");
            $('#inputMessageAdmob43').val("");
            $('#inputMessageAdmob44').val("");
        }
        return false;
    });
</script>
</body>
</html>