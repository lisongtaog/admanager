<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ page import="com.bestgo.admanager.utils.NumberUtil" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="com.bestgo.admanager.servlet.CampaignAdmob" %>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp" %>

<html>
<head>
    <title>Admob广告系列管理</title>
</head>
<body>

<%

    Object object = session.getAttribute("isAdmin");
    if (object == null) {
        response.sendRedirect("login.jsp");
    }
%>

<div class="container-fluid">
    <%@include file="common/navigationbar.jsp" %>

    <div class="panel panel-default">
        <div class="panel-heading">
            <button id="btnNotExistTagAdmobSearch" class="btn btn-default" name="false">只显示没有加上标签的广告系列</button>
            &nbsp;&nbsp;
            <input id="inputSearch" class="form-control" placeholder="系列名字或系列ID，系列名字可以模糊查询"
                   style="display: inline; width: auto;" type="text"/>
            <button id="btnSearch" class="btn btn-default glyphicon glyphicon-search"></button>
        </div>

        <table class="table">
            <thead>
            <tr>
                <th>序号</th>
                <th>系列ID</th>
                <th>广告账号ID</th>
                <th>系列名称</th>
                <th>创建时间</th>
                <th>状态</th>
                <th>预算</th>
                <th>竞价</th>
                <th>总花费</th>
                <th>总安装</th>
                <th>总点击</th>
                <th>CPA</th>
                <th>CTR</th>
                <th>CVR</th>
                <th>标签</th>
                <th>国家</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>

            <%
                List<JSObject> data = new ArrayList<>();
                long totalPage = 0;
                long count = CampaignAdmob.count();
                int index = NumberUtil.parseInt(request.getParameter("page_index"), 0);
                int size = NumberUtil.parseInt(request.getParameter("page_size"), 20);
                totalPage = count / size + (count % size == 0 ? 0 : 1);

                int preIndex = index > 0 ? index - 1 : 0;
                int nextPage = index < totalPage - 1 ? index + 1 : index;

                data = CampaignAdmob.fetchData(index, size);

                List<JSObject> allTags = Tags.fetchAllTags();
                JsonArray array = new JsonArray();
                for (int i = 0; i < allTags.size(); i++) {
                    array.add((String) allTags.get(i).get("tag_name"));
                }

            %>

            <%
                for (int i = 0; i < data.size(); i++) {
                    JSObject one = data.get(i);
                    List<String> tags = CampaignAdmob.bindTags((String) one.get("campaign_id"));
                    String tagStr = "";
                    for (int ii = 0; ii < tags.size(); ii++) {
                        tagStr += (tags.get(ii) + ",");
                    }
                    if (tagStr.length() > 0) {
                        tagStr = tagStr.substring(0, tagStr.length() - 1);
                    }
                    double installed = NumberUtil.convertDouble(one.get("total_installed"), 0);
                    double click = NumberUtil.convertDouble(one.get("total_click"), 0);
                    double cvr = click > 0 ? installed / click : 0;
            %>
            <tr>
                <td><%=one.get("id")%></td>
                <td><%=one.get("campaign_id")%></td>
                <td><%=one.get("account_id")%></td>
                <td><%=one.get("campaign_name")%></td>
                <td><%=one.get("create_time")%></td>
                <td><%=one.get("status")%></td>
                <td><%=(double)one.get("budget") / 100 %></td>
                <td><%=(double)one.get("bidding") / 100%></td>
                <td><%=one.get("total_spend")%></td>
                <td><%=one.get("total_installed")%></td>
                <td><%=one.get("total_click")%></td>
                <td><fmt:formatNumber value='<%=one.get("cpa")%>' pattern="0.0000"/> </td>
                <td><fmt:formatNumber value='<%=one.get("ctr")%>' pattern="0.0000"/> </td>
                <td><fmt:formatNumber value="<%=cvr%>" pattern="0.0000"/> </td>
                <td><%=tagStr%></td>
                <td><%=one.get("country_code")%></td>
                <td><a class="link_modify" href="javascript:void(0)"><span class="glyphicon glyphicon-pencil"></span></a></td>
            </tr>
            <% } %>

            </tbody>
        </table>

        <nav aria-label="Page navigation">
            <ul class="pagination">
                <li>
                    <a href="campaigns_admob.jsp?page_index=<%=preIndex%>" aria-label="Previous">
                        <span aria-hidden="true">上一页</span>
                    </a>
                </li>
                <li>
                    <a href="campaigns_admob.jsp?page_index=<%=nextPage%>" aria-label="Next">
                        <span aria-hidden="true">下一页</span>
                    </a>
                </li>
                <li>
                    共<%=totalPage%>页
                </li>
            </ul>
        </nav>
    </div>
</div>

<div id="new_campaign_dlg" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="dlg_title">修改系列</h4>
            </div>
            <div class="modal-body">
                <form id="modify_form" class="form-horizontal" action="#" autocomplete="off">
                    <div class="form-group">
                        <label for="inputTags" class="col-sm-2 control-label">标签</label>
                        <div class="col-sm-10">
                            <input data-role="tagsinput" class="form-control" id="inputTags" placeholder="标签"
                                   autocomplete="off">
                        </div>
                    </div>


                    <div class="form-group">
                        <label for="selectRegionAdmob" class="col-sm-2 control-label">国家</label>
                        <div class="col-sm-10">
                            <select class="form-control select2" id="selectRegionAdmob" multiple="multiple"
                                    style="height: 33px;width: 460px">
                            </select>
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

<jsp:include page="common/loading_dialog.jsp"></jsp:include>

<script src="common/statics/js/typeahead.js"></script>
<script src="common/statics/bootstrap/js/bootstrap-tagsinput.js"></script>

<script type="text/javascript">
    var admobRegionCodes = {
        "Sint Maarten": "SX",
        "Cuba": "CU",
        "Curacao": "CW",
        "Iran": "IR",
        "Afghanistan": "AF",
        "Albania": "AL",
        "Antarctica": "AQ",
        "Algeria": "DZ",
        "American Samoa": "AS",
        "Andorra": "AD",
        "Angola": "AO",
        "Antigua and Barbuda": "AG",
        "Azerbaijan": "AZ",
        "Argentina": "AR",
        "Australia": "AU",
        "Austria": "AT",
        "The Bahamas": "BS",
        "Bahrain": "BH",
        "Bangladesh": "BD",
        "Armenia": "AM",
        "Barbados": "BB",
        "Belgium": "BE",
        "Bermuda": "BM",
        "Bhutan": "BT",
        "Bolivia": "BO",
        "Bosnia and Herzegovina": "BA",
        "Botswana": "BW",
        "Bouvet Island": "BV",
        "Brazil": "BR",
        "Belize": "BZ",
        "British Indian Ocean Territory": "IO",
        "Solomon Islands": "SB",
        "British Virgin Islands": "VG",
        "Brunei": "BN",
        "Bulgaria": "BG",
        "Myanmar (Burma)": "MM",
        "Burundi": "BI",
        "Belarus": "BY",
        "Cambodia": "KH",
        "Cameroon": "CM",
        "Canada": "CA",
        "Cape Verde": "CV",
        "Cayman Islands": "KY",
        "Central African Republic": "CF",
        "Sri Lanka": "LK",
        "Chad": "TD",
        "Chile": "CL",
        "China": "CN",
        "Taiwan": "TW",
        "Christmas Island": "CX",
        "Cocos (Keeling) Islands": "CC",
        "Colombia": "CO",
        "Comoros": "KM",
        "Mayotte": "YT",
        "Republic of the Congo": "CG",
        "Democratic Republic of the Congo": "CD",
        "Cook Islands": "CK",
        "Costa Rica": "CR",
        "Croatia": "HR",
        "Cyprus": "CY",
        "Czechia": "CZ",
        "Benin": "BJ",
        "Denmark": "DK",
        "Dominica": "DM",
        "Dominican Republic": "DO",
        "Ecuador": "EC",
        "El Salvador": "SV",
        "Equatorial Guinea": "GQ",
        "Ethiopia": "ET",
        "Eritrea": "ER",
        "Estonia": "EE",
        "Faroe Islands": "FO",
        "Falkland Islands (Islas Malvinas)": "FK",
        "South Georgia and the South Sandwich Islands": "GS",
        "Fiji": "FJ",
        "Finland": "FI",
        "France": "FR",
        "French Guiana": "GF",
        "French Polynesia": "PF",
        "French Southern and Antarctic Lands": "TF",
        "Djibouti": "DJ",
        "Gabon": "GA",
        "Georgia": "GE",
        "The Gambia": "GM",
        "Palestine": "PS",
        "Germany": "DE",
        "Ghana": "GH",
        "Gibraltar": "GI",
        "Kiribati": "KI",
        "Greece": "GR",
        "Greenland": "GL",
        "Grenada": "GD",
        "Guadeloupe": "GP",
        "Guam": "GU",
        "Guatemala": "GT",
        "Guinea": "GN",
        "Guyana": "GY",
        "Haiti": "HT",
        "Heard Island and McDonald Islands": "HM",
        "Vatican City": "VA",
        "Honduras": "HN",
        "Hong Kong": "HK",
        "Hungary": "HU",
        "Iceland": "IS",
        "India": "IN",
        "Indonesia": "ID",
        "Iraq": "IQ",
        "Ireland": "IE",
        "Israel": "IL",
        "Italy": "IT",
        "Cote d'Ivoire": "CI",
        "Jamaica": "JM",
        "Japan": "JP",
        "Kazakhstan": "KZ",
        "Jordan": "JO",
        "Kenya": "KE",
        "South Korea": "KR",
        "Kuwait": "KW",
        "Kyrgyzstan": "KG",
        "Laos": "LA",
        "Lebanon": "LB",
        "Lesotho": "LS",
        "Latvia": "LV",
        "Liberia": "LR",
        "Libya": "LY",
        "Liechtenstein": "LI",
        "Lithuania": "LT",
        "Luxembourg": "LU",
        "Macau": "MO",
        "Madagascar": "MG",
        "Malawi": "MW",
        "Malaysia": "MY",
        "Maldives": "MV",
        "Mali": "ML",
        "Malta": "MT",
        "Martinique": "MQ",
        "Mauritania": "MR",
        "Mauritius": "MU",
        "Mexico": "MX",
        "Monaco": "MC",
        "Mongolia": "MN",
        "Moldova": "MD",
        "Montenegro": "ME",
        "Montserrat": "MS",
        "Morocco": "MA",
        "Mozambique": "MZ",
        "Oman": "OM",
        "Namibia": "NA",
        "Nauru": "NR",
        "Nepal": "NP",
        "Netherlands": "NL",
        "Netherlands Antilles": "BQ",
        "Aruba": "AW",
        "New Caledonia": "NC",
        "Vanuatu": "VU",
        "New Zealand": "NZ",
        "Nicaragua": "NI",
        "Niger": "NE",
        "Nigeria": "NG",
        "Niue": "NU",
        "Norfolk Island": "NF",
        "Norway": "NO",
        "Northern Mariana Islands": "MP",
        "United States Minor Outlying Islands": "UM",
        "Federated States of Micronesia": "FM",
        "Marshall Islands": "MH",
        "Palau": "PW",
        "Pakistan": "PK",
        "Panama": "PA",
        "Papua New Guinea": "PG",
        "Paraguay": "PY",
        "Peru": "PE",
        "Philippines": "PH",
        "Pitcairn Islands": "PN",
        "Poland": "PL",
        "Portugal": "PT",
        "Guinea-Bissau": "GW",
        "Timor-Leste": "TL",
        "Puerto Rico": "PR",
        "Qatar": "QA",
        "Reunion": "RE",
        "Romania": "RO",
        "Russia": "RU",
        "Rwanda": "RW",
        "Saint Helena, Ascension and Tristan da Cunha": "SH",
        "Saint Kitts and Nevis": "KN",
        "Anguilla": "AI",
        "Saint Lucia": "LC",
        "Saint Pierre and Miquelon": "PM",
        "Saint Vincent and the Grenadines": "VC",
        "San Marino": "SM",
        "Sao Tome and Principe": "ST",
        "Saudi Arabia": "SA",
        "Senegal": "SN",
        "Serbia": "RS",
        "Seychelles": "SC",
        "Sierra Leone": "SL",
        "Singapore": "SG",
        "Slovakia": "SK",
        "Vietnam": "VN",
        "Slovenia": "SI",
        "Somalia": "SO",
        "South Africa": "ZA",
        "Zimbabwe": "ZW",
        "Spain": "ES",
        "Western Sahara": "EH",
        "Suriname": "SR",
        "Svalbard and Jan Mayen": "SJ",
        "Swaziland": "SZ",
        "Sweden": "SE",
        "Switzerland": "CH",
        "Tajikistan": "TJ",
        "Thailand": "TH",
        "Togo": "TG",
        "Tokelau": "TK",
        "Tonga": "TO",
        "Trinidad and Tobago": "TT",
        "United Arab Emirates": "AE",
        "Tunisia": "TN",
        "Turkey": "TR",
        "Turkmenistan": "TM",
        "Turks and Caicos Islands": "TC",
        "Tuvalu": "TV",
        "Uganda": "UG",
        "Ukraine": "UA",
        "Macedonia (FYROM)": "MK",
        "Egypt": "EG",
        "United Kingdom": "GB",
        "Guernsey": "GG",
        "Jersey": "JE",
        "Tanzania": "TZ",
        "United States": "US",
        "U.S. Virgin Islands": "VI",
        "Burkina Faso": "BF",
        "Uruguay": "UY",
        "Uzbekistan": "UZ",
        "Venezuela": "VE",
        "Wallis and Futuna": "WF",
        "Samoa": "WS",
        "Yemen": "YE",
        "Zambia": "ZM",
        "Kosovo": "XK"
    };

    function init() {
        $('.select2').select2();
        for (var k in admobRegionCodes) {
            var key, value;
            key = k;
            value = admobRegionCodes[k];
            $('#selectRegionAdmob').append($("<option value='" + value + "'>" + key + "</option>"));
        }
    }

    init();
    $("li[role='presentation']:eq(3)").addClass("active");
    var id;
    var campaignId;

    var data = <%=array.toString()%>;

    var tagNames = new Bloodhound({
        datumTokenizer: Bloodhound.tokenizers.obj.whitespace('name'),
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        local: $.map(data, function (tag) {
            return {
                name: tag
            };
        })
    });
    tagNames.initialize();

    $('#inputTags').tagsinput({
        typeaheadjs: [{
            minLength: 1,
            highlight: true,
        }, {
            minlength: 1,
            name: 'name',
            displayKey: 'name',
            valueKey: 'name',
            source: tagNames.ttAdapter()
        }],
        freeInput: false
    });

    $("#new_campaign_dlg .btn-primary").click(function () {
        var tags = $('#inputTags').val();
        var selectRegionAdmob = $('#selectRegionAdmob').val();

        $.post('campaign_admob/update', {
            id: id.trim(),
            campaignId: campaignId.trim(),
            tags: tags,
            selectRegionAdmob: selectRegionAdmob.join(",")
        }, function (data) {
            if (data && data.ret == 1) {
                $("#new_campaign_dlg").modal("hide");
//                location.reload();
                $('#btnNotExistTagAdmobSearch').prop("name", false);
                $('#btnNotExistTagAdmobSearch').click();
            } else {
                admanager.showCommonDlg("错误", data.message);
            }
        }, 'json');
    });

    $('#btnSearch').click(function () {
        var query = $("#inputSearch").val();
        $.post('campaign_admob/query', {
            word: query,
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

    $('#btnNotExistTagAdmobSearch').click(function () {
        if (this.name == "false") {
            $('#btnNotExistTagAdmobSearch').css("background-color", "red");
            this.name = "true";
            $('.table tbody > tr').remove();
            $.post('campaign_admob/fetch_campaigns_not_exist_tag', function (data) {
                if (data && data.ret == 1) {
                    setData(data.data);
                    bindOp();
                    $("nav").empty();
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        } else {
            this.name = "false";
            window.location.reload();
        }
    });

    function setData(data) {
        for (var i = 0; i < data.length; i++) {
            var one = data[i];
            var tr = $('<tr></tr>');
            var keyset = ["id", "campaign_id", "account_id", "campaign_name", "create_time",
                "status", "budget", "bidding", "total_spend", "total_installed", "total_click", "cpa", "ctr", "cvr", "tag_id", "country_code"];
            for (var j = 0; j < keyset.length; j++) {
                var td = $('<td></td>');
                if (keyset[j] == 'budget' || keyset[j] == 'bidding') {
                    td.text(one[keyset[j]] / 100);
                } else {
                    td.text(one[keyset[j]]);
                }
                tr.append(td);
            }
            td = $('<td><a class="link_modify" href="javascript:void(0)"><span class="glyphicon glyphicon-pencil"></span></a>');
            tr.append(td);
            $('.table tbody').append(tr);
        }
    }

    function bindOp() {
        $(".link_modify").click(function () {

            $('#modify_form').show();

            $("#dlg_title").text("修改系列");

            var tds = $(this).parents("tr").find('td');
            id = $(tds.get(0)).text();
            campaignId = $(tds.get(1)).text();
            var tags = $(tds.get(14)).text().split(',');

            var countryCodeString = $(tds.get(15)).text().trim();
            var countryCode = countryCodeString.split(",");
            $('#selectRegionAdmob').val(countryCode);
            $("#selectRegionAdmob").trigger("change");

            $('#inputTags').tagsinput('removeAll');
            for (var i = 0; i < tags.length; i++) {
                if (tags[i] != '') {
                    $('#inputTags').tagsinput('add', tags[i]);
                }
            }

            $("#new_campaign_dlg").modal("show");
        });
    }

    bindOp();
</script>
<script src="js/interlaced-color-change.js"></script>
</body>
</html>
