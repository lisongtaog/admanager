<%@ page import="com.bestgo.admanager.utils.NumberUtil" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.common.database.services.DB" %>
<%@ page import="com.bestgo.admanager.servlet.TagsBidAdmanager" %>
<%@ page import="com.google.gson.JsonArray" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp" %>


<html>
<head>
    <title>标签下国家出价管理</title>
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

    //添加按钮数据的加载
    List<JSObject> allTags = Tags.fetchAllTags();
    JsonArray array1 = new JsonArray();
    for (int i = 0; i < allTags.size(); i++) {
        array1.add((String) allTags.get(i).get("tag_name"));
    }
%>

<div class="container-fluid">
    <%@include file="common/navigationbar.jsp" %>

    <div class="panel panel-default">
        <!-- Default panel contents -->
        <div class="panel-heading">
            <button id="btn_add_new_tag" class="btn btn-default">添加</button>
            <input id="inputSearch" class="form-control" style="display: inline; width: auto;" type="text"/>
            <button id="btnSearch" class="btn btn-default glyphicon glyphicon-search"></button>
        </div>

        <table class="table">
            <thead>
            <tr>
                <th>标签ID</th>
                <th>标签名称</th>
                <th>国家</th>
                <th>出价</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>

            <%
                //页面刷新，加载全部的数据。
                List<JSObject> data = new ArrayList<>();
                long totalPage = 0;
                long count = TagsBidAdmanager.count();
                int index = NumberUtil.parseInt(request.getParameter("page_index"), 0);
                int size = NumberUtil.parseInt(request.getParameter("page_size"), 20);
                totalPage = count / size + (count % size == 0 ? 0 : 1);

                int preIndex = index > 0 ? index - 1 : 0;
                int nextPage = index < totalPage - 1 ? index + 1 : index;

                data = TagsBidAdmanager.fetchAllData(index, size);
            %>

            <%
                for (int i = 0; i < data.size(); i++) {
                    JSObject one = data.get(i);
            %>
            <tr>
                <td><%=one.get("id")%></td>
                <td><%=one.get("tag_name")%></td>
                <td><%=one.get("country_name")%></td>
                <td><%=one.get("bidding")%></td>
                <td><a class="link_modify glyphicon glyphicon-pencil" href="#"></a><a
                        class="link_delete glyphicon glyphicon-remove" href="#"></a></td>
            </tr>
            <% } %>

            </tbody>
        </table>

        <nav aria-label="Page navigation">
            <ul class="pagination">
                <li>
                    <a href="tags.jsp?page_index=<%=preIndex%>" aria-label="Previous">
                        <span aria-hidden="true">上一页</span>
                    </a>
                </li>
                <%
                    for (int i = 0; i < totalPage; i++) {
                %>
                <li><a href="tags.jsp?page_index=<%=i%>"><span><%=i + 1%></span></a></li>
                <% } %>
                <li>
                    <a href="tags.jsp?page_index=<%=nextPage%>" aria-label="Next">
                        <span aria-hidden="true">下一页</span>
                    </a>
                </li>
            </ul>
        </nav>
    </div>
</div>

<div id="new_tag_dlg" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="dlg_title">标签</h4>
            </div>
            <div class="modal-body">
                <form id="modify_form" class="form-horizontal" action="#" autocomplete="off">
                    <div class="form-group">
                        <label for="inputTagName" class="col-sm-2 control-label">标签名称</label>
                        <div class="col-sm-8">
                            <input class="form-control" id="inputTagName" placeholder="标签名称" type="text"
                                   style="display: inline;">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="inputCountry" class="col-sm-2 control-label">国家地区</label>
                        <div class="col-sm-8">
                            <input class="form-control" id="inputCountry" placeholder="国家" type="text"
                                   style="display: inline;" type="text">
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="inputBidding" class="col-sm-2 control-label">出价</label>
                        <div class="col-sm-8">
                            <input class="form-control" id="inputBidding" placeholder="出价" autocomplete="off">
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
    //页面刷新，标签名称自动加载
    var data1 = <%=array1.toString()%>;
    $("#inputTagName").autocomplete({
        source: data1
    });

    //页面刷新，国家地区自动加载
    var regionList = [{"name": "Worldwide"}, {
        "key": "AD",
        "name": "Andorra",
        "type": "country",
        "country_code": "AD",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "AE",
        "name": "United Arab Emirates",
        "type": "country",
        "country_code": "AE",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "AF",
        "name": "Afghanistan",
        "type": "country",
        "country_code": "AF",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "AG",
        "name": "Antigua",
        "type": "country",
        "country_code": "AG",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "AI",
        "name": "Anguilla",
        "type": "country",
        "country_code": "AI",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "AL",
        "name": "Albania",
        "type": "country",
        "country_code": "AL",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "AM",
        "name": "Armenia",
        "type": "country",
        "country_code": "AM",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "AN",
        "name": "Netherlands Antilles",
        "type": "country",
        "country_code": "AN",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "AO",
        "name": "Angola",
        "type": "country",
        "country_code": "AO",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "AQ",
        "name": "Antarctica",
        "type": "country",
        "country_code": "AQ",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "AR",
        "name": "Argentina",
        "type": "country",
        "country_code": "AR",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "AS",
        "name": "American Samoa",
        "type": "country",
        "country_code": "AS",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "AT",
        "name": "Austria",
        "type": "country",
        "country_code": "AT",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "AU",
        "name": "Australia",
        "type": "country",
        "country_code": "AU",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "AW",
        "name": "Aruba",
        "type": "country",
        "country_code": "AW",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "AX",
        "name": "Aland Islands",
        "type": "country",
        "country_code": "AX",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "AZ",
        "name": "Azerbaijan",
        "type": "country",
        "country_code": "AZ",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "BA",
        "name": "Bosnia and Herzegovina",
        "type": "country",
        "country_code": "BA",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "BB",
        "name": "Barbados",
        "type": "country",
        "country_code": "BB",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "BD",
        "name": "Bangladesh",
        "type": "country",
        "country_code": "BD",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "BE",
        "name": "Belgium",
        "type": "country",
        "country_code": "BE",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "BF",
        "name": "Burkina Faso",
        "type": "country",
        "country_code": "BF",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "BG",
        "name": "Bulgaria",
        "type": "country",
        "country_code": "BG",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "BH",
        "name": "Bahrain",
        "type": "country",
        "country_code": "BH",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "BI",
        "name": "Burundi",
        "type": "country",
        "country_code": "BI",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "BJ",
        "name": "Benin",
        "type": "country",
        "country_code": "BJ",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "BL",
        "name": "Saint Barthélemy",
        "type": "country",
        "country_code": "BL",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "BM",
        "name": "Bermuda",
        "type": "country",
        "country_code": "BM",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "BN",
        "name": "Brunei",
        "type": "country",
        "country_code": "BN",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "BO",
        "name": "Bolivia",
        "type": "country",
        "country_code": "BO",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "BQ",
        "name": "Bonaire, Sint Eustatius and Saba",
        "type": "country",
        "country_code": "BQ",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "BR",
        "name": "Brazil",
        "type": "country",
        "country_code": "BR",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "BS",
        "name": "The Bahamas",
        "type": "country",
        "country_code": "BS",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "BT",
        "name": "Bhutan",
        "type": "country",
        "country_code": "BT",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "BV",
        "name": "Bouvet Island",
        "type": "country",
        "country_code": "BV",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "BW",
        "name": "Botswana",
        "type": "country",
        "country_code": "BW",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "BY",
        "name": "Belarus",
        "type": "country",
        "country_code": "BY",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "BZ",
        "name": "Belize",
        "type": "country",
        "country_code": "BZ",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "CA",
        "name": "Canada",
        "type": "country",
        "country_code": "CA",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "CC",
        "name": "Cocos (Keeling) Islands",
        "type": "country",
        "country_code": "CC",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "CD",
        "name": "Democratic Republic of the Congo",
        "type": "country",
        "country_code": "CD",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "CF",
        "name": "Central African Republic",
        "type": "country",
        "country_code": "CF",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "CG",
        "name": "Republic of the Congo",
        "type": "country",
        "country_code": "CG",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "CH",
        "name": "Switzerland",
        "type": "country",
        "country_code": "CH",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "CI",
        "name": "Côte d'Ivoire",
        "type": "country",
        "country_code": "CI",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "CK",
        "name": "Cook Islands",
        "type": "country",
        "country_code": "CK",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "CL",
        "name": "Chile",
        "type": "country",
        "country_code": "CL",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "CM",
        "name": "Cameroon",
        "type": "country",
        "country_code": "CM",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "CN",
        "name": "China",
        "type": "country",
        "country_code": "CN",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "CO",
        "name": "Colombia",
        "type": "country",
        "country_code": "CO",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "CR",
        "name": "Costa Rica",
        "type": "country",
        "country_code": "CR",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "CV",
        "name": "Cape Verde",
        "type": "country",
        "country_code": "CV",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "CW",
        "name": "Curaçao",
        "type": "country",
        "country_code": "CW",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "CX",
        "name": "Christmas Island",
        "type": "country",
        "country_code": "CX",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "CY",
        "name": "Cyprus",
        "type": "country",
        "country_code": "CY",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "CZ",
        "name": "Czech Republic",
        "type": "country",
        "country_code": "CZ",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "DE",
        "name": "Germany",
        "type": "country",
        "country_code": "DE",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "DJ",
        "name": "Djibouti",
        "type": "country",
        "country_code": "DJ",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "DK",
        "name": "Denmark",
        "type": "country",
        "country_code": "DK",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "DM",
        "name": "Dominica",
        "type": "country",
        "country_code": "DM",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "DO",
        "name": "Dominican Republic",
        "type": "country",
        "country_code": "DO",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "DZ",
        "name": "Algeria",
        "type": "country",
        "country_code": "DZ",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "EC",
        "name": "Ecuador",
        "type": "country",
        "country_code": "EC",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "EE",
        "name": "Estonia",
        "type": "country",
        "country_code": "EE",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "EG",
        "name": "Egypt",
        "type": "country",
        "country_code": "EG",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "EH",
        "name": "Western Sahara",
        "type": "country",
        "country_code": "EH",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "ER",
        "name": "Eritrea",
        "type": "country",
        "country_code": "ER",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "ES",
        "name": "Spain",
        "type": "country",
        "country_code": "ES",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "ET",
        "name": "Ethiopia",
        "type": "country",
        "country_code": "ET",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "FI",
        "name": "Finland",
        "type": "country",
        "country_code": "FI",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "FJ",
        "name": "Fiji",
        "type": "country",
        "country_code": "FJ",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "FK",
        "name": "Falkland Islands",
        "type": "country",
        "country_code": "FK",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "FM",
        "name": "Federated States of Micronesia",
        "type": "country",
        "country_code": "FM",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "FO",
        "name": "Faroe Islands",
        "type": "country",
        "country_code": "FO",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "FR",
        "name": "France",
        "type": "country",
        "country_code": "FR",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "GA",
        "name": "Gabon",
        "type": "country",
        "country_code": "GA",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "GB",
        "name": "United Kingdom",
        "type": "country",
        "country_code": "GB",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "GD",
        "name": "Grenada",
        "type": "country",
        "country_code": "GD",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "GE",
        "name": "Georgia",
        "type": "country",
        "country_code": "GE",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "GF",
        "name": "French Guiana",
        "type": "country",
        "country_code": "GF",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "GG",
        "name": "Guernsey",
        "type": "country",
        "country_code": "GG",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "GH",
        "name": "Ghana",
        "type": "country",
        "country_code": "GH",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "GI",
        "name": "Gibraltar",
        "type": "country",
        "country_code": "GI",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "GL",
        "name": "Greenland",
        "type": "country",
        "country_code": "GL",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "GM",
        "name": "The Gambia",
        "type": "country",
        "country_code": "GM",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "GN",
        "name": "Guinea",
        "type": "country",
        "country_code": "GN",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "GP",
        "name": "Guadeloupe",
        "type": "country",
        "country_code": "GP",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "GQ",
        "name": "Equatorial Guinea",
        "type": "country",
        "country_code": "GQ",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "GR",
        "name": "Greece",
        "type": "country",
        "country_code": "GR",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "GS",
        "name": "South Georgia and the South Sandwich Islands",
        "type": "country",
        "country_code": "GS",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "GT",
        "name": "Guatemala",
        "type": "country",
        "country_code": "GT",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "GU",
        "name": "Guam",
        "type": "country",
        "country_code": "GU",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "GW",
        "name": "Guinea-Bissau",
        "type": "country",
        "country_code": "GW",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "GY",
        "name": "Guyana",
        "type": "country",
        "country_code": "GY",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "HK",
        "name": "Hong Kong",
        "type": "country",
        "country_code": "HK",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "HM",
        "name": "Heard Island and McDonald Islands",
        "type": "country",
        "country_code": "HM",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "HN",
        "name": "Honduras",
        "type": "country",
        "country_code": "HN",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "HR",
        "name": "Croatia",
        "type": "country",
        "country_code": "HR",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "HT",
        "name": "Haiti",
        "type": "country",
        "country_code": "HT",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "HU",
        "name": "Hungary",
        "type": "country",
        "country_code": "HU",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "ID",
        "name": "Indonesia",
        "type": "country",
        "country_code": "ID",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "IE",
        "name": "Ireland",
        "type": "country",
        "country_code": "IE",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "IL",
        "name": "Israel",
        "type": "country",
        "country_code": "IL",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "IM",
        "name": "Isle Of Man",
        "type": "country",
        "country_code": "IM",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "IN",
        "name": "India",
        "type": "country",
        "country_code": "IN",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "IO",
        "name": "British Indian Ocean Territory",
        "type": "country",
        "country_code": "IO",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "IQ",
        "name": "Iraq",
        "type": "country",
        "country_code": "IQ",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "IS",
        "name": "Iceland",
        "type": "country",
        "country_code": "IS",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "IT",
        "name": "Italy",
        "type": "country",
        "country_code": "IT",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "JE",
        "name": "Jersey",
        "type": "country",
        "country_code": "JE",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "JM",
        "name": "Jamaica",
        "type": "country",
        "country_code": "JM",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "JO",
        "name": "Jordan",
        "type": "country",
        "country_code": "JO",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "JP",
        "name": "Japan",
        "type": "country",
        "country_code": "JP",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "KE",
        "name": "Kenya",
        "type": "country",
        "country_code": "KE",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "KG",
        "name": "Kyrgyzstan",
        "type": "country",
        "country_code": "KG",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "KH",
        "name": "Cambodia",
        "type": "country",
        "country_code": "KH",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "KI",
        "name": "Kiribati",
        "type": "country",
        "country_code": "KI",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "KM",
        "name": "Comoros",
        "type": "country",
        "country_code": "KM",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "KN",
        "name": "Saint Kitts and Nevis",
        "type": "country",
        "country_code": "KN",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "KP",
        "name": "North Korea",
        "type": "country",
        "country_code": "KP",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "KR",
        "name": "South Korea",
        "type": "country",
        "country_code": "KR",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "KW",
        "name": "Kuwait",
        "type": "country",
        "country_code": "KW",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "KY",
        "name": "Cayman Islands",
        "type": "country",
        "country_code": "KY",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "KZ",
        "name": "Kazakhstan",
        "type": "country",
        "country_code": "KZ",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "LA",
        "name": "Laos",
        "type": "country",
        "country_code": "LA",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "LB",
        "name": "Lebanon",
        "type": "country",
        "country_code": "LB",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "LC",
        "name": "St. Lucia",
        "type": "country",
        "country_code": "LC",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "LI",
        "name": "Liechtenstein",
        "type": "country",
        "country_code": "LI",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "LK",
        "name": "Sri Lanka",
        "type": "country",
        "country_code": "LK",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "LR",
        "name": "Liberia",
        "type": "country",
        "country_code": "LR",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "LS",
        "name": "Lesotho",
        "type": "country",
        "country_code": "LS",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "LT",
        "name": "Lithuania",
        "type": "country",
        "country_code": "LT",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "LU",
        "name": "Luxembourg",
        "type": "country",
        "country_code": "LU",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "LV",
        "name": "Latvia",
        "type": "country",
        "country_code": "LV",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "LY",
        "name": "Libya",
        "type": "country",
        "country_code": "LY",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "MA",
        "name": "Morocco",
        "type": "country",
        "country_code": "MA",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "MC",
        "name": "Monaco",
        "type": "country",
        "country_code": "MC",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "MD",
        "name": "Moldova",
        "type": "country",
        "country_code": "MD",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "ME",
        "name": "Montenegro",
        "type": "country",
        "country_code": "ME",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "MF",
        "name": "Saint Martin",
        "type": "country",
        "country_code": "MF",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "MG",
        "name": "Madagascar",
        "type": "country",
        "country_code": "MG",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "MH",
        "name": "Marshall Islands",
        "type": "country",
        "country_code": "MH",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "MK",
        "name": "Macedonia",
        "type": "country",
        "country_code": "MK",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "ML",
        "name": "Mali",
        "type": "country",
        "country_code": "ML",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "MM",
        "name": "Myanmar",
        "type": "country",
        "country_code": "MM",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "MN",
        "name": "Mongolia",
        "type": "country",
        "country_code": "MN",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "MO",
        "name": "Macau",
        "type": "country",
        "country_code": "MO",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "MP",
        "name": "Northern Mariana Islands",
        "type": "country",
        "country_code": "MP",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "MQ",
        "name": "Martinique",
        "type": "country",
        "country_code": "MQ",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "MR",
        "name": "Mauritania",
        "type": "country",
        "country_code": "MR",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "MS",
        "name": "Montserrat",
        "type": "country",
        "country_code": "MS",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "MT",
        "name": "Malta",
        "type": "country",
        "country_code": "MT",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "MU",
        "name": "Mauritius",
        "type": "country",
        "country_code": "MU",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "MV",
        "name": "Maldives",
        "type": "country",
        "country_code": "MV",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "MW",
        "name": "Malawi",
        "type": "country",
        "country_code": "MW",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "MX",
        "name": "Mexico",
        "type": "country",
        "country_code": "MX",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "MY",
        "name": "Malaysia",
        "type": "country",
        "country_code": "MY",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "MZ",
        "name": "Mozambique",
        "type": "country",
        "country_code": "MZ",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "NA",
        "name": "Namibia",
        "type": "country",
        "country_code": "NA",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "NC",
        "name": "New Caledonia",
        "type": "country",
        "country_code": "NC",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "NE",
        "name": "Niger",
        "type": "country",
        "country_code": "NE",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "NF",
        "name": "Norfolk Island",
        "type": "country",
        "country_code": "NF",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "NG",
        "name": "Nigeria",
        "type": "country",
        "country_code": "NG",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "NI",
        "name": "Nicaragua",
        "type": "country",
        "country_code": "NI",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "NL",
        "name": "Netherlands",
        "type": "country",
        "country_code": "NL",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "NO",
        "name": "Norway",
        "type": "country",
        "country_code": "NO",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "NP",
        "name": "Nepal",
        "type": "country",
        "country_code": "NP",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "NR",
        "name": "Nauru",
        "type": "country",
        "country_code": "NR",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "NU",
        "name": "Niue",
        "type": "country",
        "country_code": "NU",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "NZ",
        "name": "New Zealand",
        "type": "country",
        "country_code": "NZ",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "OM",
        "name": "Oman",
        "type": "country",
        "country_code": "OM",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "PA",
        "name": "Panama",
        "type": "country",
        "country_code": "PA",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "PE",
        "name": "Peru",
        "type": "country",
        "country_code": "PE",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "PF",
        "name": "French Polynesia",
        "type": "country",
        "country_code": "PF",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "PG",
        "name": "Papua New Guinea",
        "type": "country",
        "country_code": "PG",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "PH",
        "name": "Philippines",
        "type": "country",
        "country_code": "PH",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "PK",
        "name": "Pakistan",
        "type": "country",
        "country_code": "PK",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "PL",
        "name": "Poland",
        "type": "country",
        "country_code": "PL",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "PM",
        "name": "Saint Pierre and Miquelon",
        "type": "country",
        "country_code": "PM",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "PN",
        "name": "Pitcairn",
        "type": "country",
        "country_code": "PN",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "PR",
        "name": "Puerto Rico",
        "type": "country",
        "country_code": "PR",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "PS",
        "name": "Palestine",
        "type": "country",
        "country_code": "PS",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "PT",
        "name": "Portugal",
        "type": "country",
        "country_code": "PT",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "PW",
        "name": "Palau",
        "type": "country",
        "country_code": "PW",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "PY",
        "name": "Paraguay",
        "type": "country",
        "country_code": "PY",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "QA",
        "name": "Qatar",
        "type": "country",
        "country_code": "QA",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "RE",
        "name": "Réunion",
        "type": "country",
        "country_code": "RE",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "RO",
        "name": "Romania",
        "type": "country",
        "country_code": "RO",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "RS",
        "name": "Serbia",
        "type": "country",
        "country_code": "RS",
        "supports_region": false,
        "supports_city": true
    }, {
        "key": "RU",
        "name": "Russia",
        "type": "country",
        "country_code": "RU",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "RW",
        "name": "Rwanda",
        "type": "country",
        "country_code": "RW",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "SA",
        "name": "Saudi Arabia",
        "type": "country",
        "country_code": "SA",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "SB",
        "name": "Solomon Islands",
        "type": "country",
        "country_code": "SB",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "SC",
        "name": "Seychelles",
        "type": "country",
        "country_code": "SC",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "SE",
        "name": "Sweden",
        "type": "country",
        "country_code": "SE",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "SG",
        "name": "Singapore",
        "type": "country",
        "country_code": "SG",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "SH",
        "name": "Saint Helena",
        "type": "country",
        "country_code": "SH",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "SI",
        "name": "Slovenia",
        "type": "country",
        "country_code": "SI",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "SJ",
        "name": "Svalbard and Jan Mayen",
        "type": "country",
        "country_code": "SJ",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "SK",
        "name": "Slovakia",
        "type": "country",
        "country_code": "SK",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "SL",
        "name": "Sierra Leone",
        "type": "country",
        "country_code": "SL",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "SM",
        "name": "San Marino",
        "type": "country",
        "country_code": "SM",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "SN",
        "name": "Senegal",
        "type": "country",
        "country_code": "SN",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "SO",
        "name": "Somalia",
        "type": "country",
        "country_code": "SO",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "SR",
        "name": "Suriname",
        "type": "country",
        "country_code": "SR",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "SS",
        "name": "South Sudan",
        "type": "country",
        "country_code": "SS",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "ST",
        "name": "Sao Tome and Principe",
        "type": "country",
        "country_code": "ST",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "SV",
        "name": "El Salvador",
        "type": "country",
        "country_code": "SV",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "SX",
        "name": "Sint Maarten",
        "type": "country",
        "country_code": "SX",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "SZ",
        "name": "Swaziland",
        "type": "country",
        "country_code": "SZ",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "TC",
        "name": "Turks and Caicos Islands",
        "type": "country",
        "country_code": "TC",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "TD",
        "name": "Chad",
        "type": "country",
        "country_code": "TD",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "TF",
        "name": "French Southern Territories",
        "type": "country",
        "country_code": "TF",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "TG",
        "name": "Togo",
        "type": "country",
        "country_code": "TG",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "TH",
        "name": "Thailand",
        "type": "country",
        "country_code": "TH",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "TJ",
        "name": "Tajikistan",
        "type": "country",
        "country_code": "TJ",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "TK",
        "name": "Tokelau",
        "type": "country",
        "country_code": "TK",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "TL",
        "name": "Timor-Leste",
        "type": "country",
        "country_code": "TL",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "TM",
        "name": "Turkmenistan",
        "type": "country",
        "country_code": "TM",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "TN",
        "name": "Tunisia",
        "type": "country",
        "country_code": "TN",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "TO",
        "name": "Tonga",
        "type": "country",
        "country_code": "TO",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "TR",
        "name": "Turkey",
        "type": "country",
        "country_code": "TR",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "TT",
        "name": "Trinidad and Tobago",
        "type": "country",
        "country_code": "TT",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "TV",
        "name": "Tuvalu",
        "type": "country",
        "country_code": "TV",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "TW",
        "name": "Taiwan",
        "type": "country",
        "country_code": "TW",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "TZ",
        "name": "Tanzania",
        "type": "country",
        "country_code": "TZ",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "UA",
        "name": "Ukraine",
        "type": "country",
        "country_code": "UA",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "UG",
        "name": "Uganda",
        "type": "country",
        "country_code": "UG",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "UM",
        "name": "United States Minor Outlying Islands",
        "type": "country",
        "country_code": "UM",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "US",
        "name": "United States",
        "type": "country",
        "country_code": "US",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "UY",
        "name": "Uruguay",
        "type": "country",
        "country_code": "UY",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "UZ",
        "name": "Uzbekistan",
        "type": "country",
        "country_code": "UZ",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "VA",
        "name": "Vatican City",
        "type": "country",
        "country_code": "VA",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "VC",
        "name": "Saint Vincent and the Grenadines",
        "type": "country",
        "country_code": "VC",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "VE",
        "name": "Venezuela",
        "type": "country",
        "country_code": "VE",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "VG",
        "name": "British Virgin Islands",
        "type": "country",
        "country_code": "VG",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "VI",
        "name": "US Virgin Islands",
        "type": "country",
        "country_code": "VI",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "VN",
        "name": "Vietnam",
        "type": "country",
        "country_code": "VN",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "VU",
        "name": "Vanuatu",
        "type": "country",
        "country_code": "VU",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "WF",
        "name": "Wallis and Futuna",
        "type": "country",
        "country_code": "WF",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "WS",
        "name": "Samoa",
        "type": "country",
        "country_code": "WS",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "XK",
        "name": "Kosovo",
        "type": "country",
        "country_code": "XK",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "YE",
        "name": "Yemen",
        "type": "country",
        "country_code": "YE",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "YT",
        "name": "Mayotte",
        "type": "country",
        "country_code": "YT",
        "supports_region": false,
        "supports_city": false
    }, {
        "key": "ZA",
        "name": "South Africa",
        "type": "country",
        "country_code": "ZA",
        "supports_region": true,
        "supports_city": true
    }, {
        "key": "ZM",
        "name": "Zambia",
        "type": "country",
        "country_code": "ZM",
        "supports_region": true,
        "supports_city": false
    }, {
        "key": "ZW",
        "name": "Zimbabwe",
        "type": "country",
        "country_code": "ZW",
        "supports_region": true,
        "supports_city": false
    }];

    var countryMap = countryMapFunction;
    var countryNames = [];

    for (var i = 0; i < regionList.length; i++) {
        countryNames.push(regionList[i].name);
    }

    $("#inputCountry").autocomplete({
        source: countryNames
    });

    var countryMapFunction = function () {
        var map = {};
        for (var i = 0; i < regionList.length; i++) {
            var item = regionList[i];
            var key = item["country_code"];
            map[key] = item["name"];
        }
        return map;
    }


    var modifyType = 'new';
    var id;
    //添加按钮事件
    $("#btn_add_new_tag").click(function () {
        modifyType = 'new';
        $("#inputTagName").val("");
        $("#inputCountry").val("");
        $("#inputBidding").val("");
        $("#inputTagName").prop("disabled", false);
        $('#delete_message').hide();
        $('#modify_form').show();
        $("#dlg_title").text("标签");
        $("#new_tag_dlg").modal("show");
    });

    $("#new_tag_dlg .btn-primary").click(function () {
        var tagName = $("#inputTagName").val();
        var country = $("#inputCountry").val();
        var bidding = $("#inputBidding").val();
        if (modifyType == 'new') {
            $.post('tagsBidAdmanager/create', {
                name: tagName,
                country: country,
                bidding: bidding
            }, function (data) {
                if (data && data.ret == 1) {
                    $("#new_tag_dlg").modal("hide");
                    alert("添加成功！")
                    location.reload();
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        } else if (modifyType == 'update') {
            $.post('tagsBidAdmanager/update', {
                id: id,
                name: tagName,
                country: country,
                bidding: bidding
            }, function (data) {
                $("#inputTagName").prop("disabled", false);
                if (data && data.ret == 1) {
                    $("#new_tag_dlg").modal("hide");
                    location.reload();
                    alert("更新成功！");
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        } else if (modifyType == 'delete') {
            $.post('tagsBidAdmanager/delete', {
                id: id
            }, function (data) {
                if (data && data.ret == 1) {
                    $("#new_tag_dlg").modal("hide");
                    location.reload();
                    alert("删除成功！");
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        }
    });

    $('#btnSearch').click(function () {
        var query = $("#inputSearch").val();
        $.post("tagsBidAdmanager/query", {
            word: query
        }, function (data) {
            if (data && data.ret == 1) {
                $('.table tbody > tr').remove();
                setData(data.data);
                bindOp();
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');
    });

    function setData(data) {
        for (var i = 0; i < data.length; i++) {
            var one = data[i];
            var tr = $('<tr></tr>');
            var td = $('<td></td>');
            td.text(one.id);
            tr.append(td);

            td = $('<td></td>');
            td.text(one.tag_name);
            tr.append(td);

            td = $('<td></td>');
            td.text(one.country);
            tr.append(td);

            td = $('<td></td>');
            td.text(one.bidding);
            tr.append(td);

            td = $('<td><a class="link_modify glyphicon glyphicon-pencil" href="#"></a><a class="link_delete glyphicon glyphicon-remove" href="#"></a></td>');
            tr.append(td);
            $('.table tbody').append(tr);
        }
    }

    function bindOp() {
        $(".link_modify").click(function () {
            modifyType = "update";
            $('#delete_message').hide();
            $('#modify_form').show();

            $("#dlg_title").text("修改");

            var tds = $(this).parents("tr").find('td');
            id = $(tds.get(0)).text();
            var tagName = $(tds.get(1)).text();
            var country = $(tds.get(2)).text();
            var bidding = $(tds.get(3)).text();

            $("#inputTagName").val(tagName).prop("disabled", true);
            $("#inputCountry").val(country);
            $("#inputBidding").val(bidding);

            $("#new_tag_dlg").modal("show");
        });

        $(".link_delete").click(function () {
            modifyType = "delete";
            $('#delete_message').show();
            $('#modify_form').hide();

            $("#dlg_title").text("删除");

            var tds = $(this).parents("tr").find('td');
            id = $(tds.get(0)).text();

            $("#new_tag_dlg").modal("show");

        });
    }

    bindOp();
</script>
<script src="js/interlaced-color-change.js"></script>
</body>
</html>
