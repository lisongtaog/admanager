<%@ page import="com.bestgo.admanager.servlet.AutoCreateCampaign" %>
<%@ page import="com.bestgo.admanager.servlet.Tags" %>
<%@ page import="com.bestgo.admanager.utils.NumberUtil" %>
<%@ page import="com.bestgo.common.database.utils.JSObject" %>
<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="static com.bestgo.admanager.servlet.AdAccount.accountIdNameRelationMap" %>
<%@ page import="static com.bestgo.admanager.servlet.AdAccountAdmob.accountIdNameAdmobRelationMap" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/rootBase.jsp" %>

<html>
<head>
    <title>自动创建系列管理</title>
    <style>
        .redline {
            background-color: #dca7a7;
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
    <div class="panel panel-default">
        <!-- Default panel contents -->
        <div class="panel-heading"><label>
            <input type="radio" name="optionsRadios" id="checkFacebook" checked>
            Facebook 广告
        </label>
            <label>
                <input type="radio" name="optionsRadios" id="checkAdwords">
                AdWords 广告
            </label>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <label for="tagName">标签</label>
            <input id="tagName" form-control style="display: inline; width: auto;" type="text">
            <label for="inputCampaignName">模糊系列名</label>
            <input id="inputCampaignName" class="form-control" style="display: inline; width: auto;" type="text"/>
            <label for="inputCountry">国家</label>
            <input id="inputCountry" form-control style="display: inline; width: auto;" type="text">
            <button id="btnSearch" class="btn btn-default glyphicon glyphicon-search"></button>
        </div>

        <%
            //先在首页显示一段信息
            List<JSObject> data = new ArrayList<>();
            long totalPage = 0;
            int preIndex = 0;
            int nextPage = 0;
            String network = request.getParameter("network");
            ArrayList<String> networks = new ArrayList<>();
            networks.add("facebook");
            networks.add("adwords");
            if (networks.indexOf(network) == -1) {
                network = "facebook";
            }
            if ("facebook".equals(network)) {
                long count = AutoCreateCampaign.facebookCount();
                int index = NumberUtil.parseInt(request.getParameter("page_index"), 0);
                int size = NumberUtil.parseInt(request.getParameter("page_size"), 20);
                totalPage = count / size + (count % size == 0 ? 0 : 1);

                preIndex = index > 0 ? index - 1 : 0;
                nextPage = index < totalPage - 1 ? index + 1 : index;

                data = AutoCreateCampaign.facebookFetchData(index, size);
            } else {
                long count = AutoCreateCampaign.adwordsCount();
                int index = NumberUtil.parseInt(request.getParameter("page_index"), 0);
                int size = NumberUtil.parseInt(request.getParameter("page_size"), 20);
                totalPage = count / size + (count % size == 0 ? 0 : 1);

                preIndex = index > 0 ? index - 1 : 0;
                nextPage = index < totalPage - 1 ? index + 1 : index;

                data = AutoCreateCampaign.adwordsFetchData(index, size);
            }
            //获取标签信息
            List<JSObject> allTags = Tags.fetchAllTags();
            JsonArray array = new JsonArray();
            for (int i = 0; i < allTags.size(); i++) {
                array.add((String) allTags.get(i).get("tag_name"));
            }
        %>

        <table class="table">
            <thead>
            <tr>
                <th>序号</th>
                <th>应用</th>
                <th>帐号</th>
                <th>国家</th>

                <%
                    if (!"adwords".equals(network)) {
                %>
                <th>语言</th>
                <th>性别</th>
                <% }%>

                <th>预算</th>
                <th>图片路径</th>

                <%
                    if (!"adwords".equals(network)) {
                %>
                <th>视频路径</th>
                <th>版位</th>
                <th>出价策略</th>
                <% }%>

                <th>出价</th>
                <th>作用域</th>
                <th>修改&nbsp;/&nbsp;<a id="batch_delete" class="glyphicon glyphicon-remove" onclick="allDelete()"></a>
                    <input type="checkbox" class="all_delete_check"></th>
                <th>开启<input class="all_checkbox_campaign_enable" type="checkbox" onclick="allChecked()"></th>
            </tr>
            </thead>
            <tbody>
            <%
                for (int i = 0; i < data.size(); i++) {
                    JSObject one = data.get(i);
                    String account_id = (String) one.get("account_id");
                    String[] account_ids = account_id.split(",");

                    String[] account_names = new String[account_ids.length];

                    for (int j = 0; j < account_ids.length; j++) {
                        if ("facebook".equals(network)) {
                            account_names[j] = accountIdNameRelationMap.get(account_ids[j]);
                        } else {
                            account_names[j] = accountIdNameAdmobRelationMap.get(account_ids[j]);
                        }
                    }
            %>
            <tr>
                <td><%=one.get("id")%>
                </td>
                <td><%=one.get("app_name")%>
                </td>
                <td><%=String.join(",", account_names)%>
                </td>
                <td><%=one.get("country_region")%>
                </td>

                <%
                    if (!"adwords".equals(network)) {
                %>
                <td><%=one.get("language")%>
                </td>
                <td><%=one.get("gender")%>
                </td>
                <% }%>

                <td><%=one.get("bugdet")%>
                </td>
                <td><%=one.get("image_path")%>
                </td>

                <%
                    if (!"adwords".equals(network)) {
                %>
                <td><%=one.get("video_path")%>
                </td>
                <td><%=one.get("publisher_platforms")%>
                </td>
                <td><%=(int) one.get("bid_strategy") == 1 ? "TARGET_COST" : "LOWEST_COST_WITH_BID_CAP"%>
                </td>
                <% }%>

                <td class="bidding"><%=one.get("bidding")%>
                </td>
                <td><%=(int) one.get("mode_type") == 0 ? "系统创建" : "次日留用"%>
                </td>
                <td><a class="link_modify" target="_blank"
                       href="campaigns_modify.jsp?type=auto_create&network=<%=network%>&id=<%=one.get("id")%>">
                    <span class="glyphicon glyphicon-pencil"></span></a>&nbsp;/&nbsp;<input type="checkbox"
                                                                                            class="delete_check"></td>
                <td><input class="checkbox_campaign_enable" type="checkbox" <% if (one.get("enabled").equals(1)) { %>
                           checked <% }%> /></td>
            </tr>
            <% } %>

            </tbody>
        </table>

        <nav aria-label="Page navigation">
            <ul class="pagination">
                <li>
                    <a class="changePage" href="campaigns_auto_create.jsp?network=<%=network%>&page_index=<%=preIndex%>"
                       aria-label="Previous">
                        <span aria-hidden="true">上一页</span>
                    </a>
                </li>
                <li>
                    <a class="changePage" href="campaigns_auto_create.jsp?network=<%=network%>&page_index=<%=nextPage%>"
                       aria-label="Next">
                        <span aria-hidden="true">下一页</span>
                    </a>
                </li>
                <li id="total_page">
                    共<%=totalPage%>页
                </li>
            </ul>
        </nav>
    </div>
</div>

<jsp:include page="common/loading_dialog.jsp"></jsp:include>

<script type="text/javascript">
    //FB和Adwords切换按钮
    var network = "<%=network%>";
    if (network == 'facebook') {
        $('#checkFacebook').prop('checked', true);
    } else {
        $('#checkAdwords').prop('checked', true);
    }
    $('#checkFacebook').click(function () {
        window.location.href = "campaigns_auto_create.jsp?network=facebook";
    });
    $('#checkAdwords').click(function () {
        window.location.href = "campaigns_auto_create.jsp?network=adwords";
    });

    //自动填充
    var tags = <%=array.toString()%>;
    $("#tagName").autocomplete({
        source: tags
    });
    var regionArray = new Array();
    regionList.forEach(function (region) {
        var countryName = region["name"];
        regionArray.push(countryName);
    });
    $("#inputCountry").autocomplete({
        source: regionArray
    });


    //条件查询事件
    $('#btnSearch').click(function () {
        var query = $("#inputCampaignName").val();
        var country = $("#inputCountry").val();
        var tagName = $("#tagName").val();
        var network = "<%=network%>";
        if (network == "adwords") {
            regionList.forEach(function (a) {
                var countryName = a["name"];
                if (countryName == country) {
                    country = a["country_code"];
                    return;
                }
            });
        }
        if (tagName == "") {
            alert("标签名不能为空！");
        } else {
            $.post('auto_create_campaign/<%=network%>/query', {
                word: query,
                country: country,
                tagName: tagName
            }, function (data) {
                if (data && data.ret == 1) {
                    $('.table tbody > tr').remove();
                    setData(data.data);
                    bindOp();
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');
        }
    });

    //批量修改出价
    $("tbody").on("click", ".bidding", function () {
        $("thead th:eq(6)").empty();
        $("thead th:eq(6)").append("出价<button id='modify_bidding' class='btn btn-link glyphicon glyphicon-pencil' title='批量修改出价' onclick='modifyBidding()'></button>");
        var elementCheck = $(this).children("input[type='text']").attr("class");
        if (elementCheck == "new_bidding") {
            return false;
        }
        var value = $(this).text();
        $(this).empty();
        $(this).append("<input class='new_bidding' type='text' style='width:60px;height:25px'value='" + value + "'>");
    });

    //批量修改事件
    function modifyBidding() {
        var bidding_array = [];
        var collection = $(".new_bidding");
        collection.each(function (idx) {
            var id = $(this).parents("tr").children("td:eq(0)").text();
            var bidding = $(this).val();
            if (!parseFloat(bidding) || parseFloat(bidding) > 0.8) {
                $(this).addClass("redline");
            } else {
                if ($(this).hasClass("redline")) {
                    $(this).removeClass("redline");
                }
            }
            var json = {};
            json.id = id;
            json.bidding = bidding;
            bidding_array.push(json);
        });
        if ($(".redline").length == 0) {
            var bidding_array_string = JSON.stringify(bidding_array);
            $.post("auto_create_campaign/<%=network%>/update_bidding", {
                bidding_array: bidding_array_string
            }, function (data) {
                if (data && data.ret == 1) {
                    admanager.showCommonDlg("成功", data.message);
                    setTimeout(function () {
                        $("#common_message_dialog").modal("hide");
                    }, 1500);
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, "json")
        } else {
            setTimeout(function () {
                $("#common_message_dialog").modal("hide");
            }, 1500);
            admanager.showCommonDlg("提示", "请检查不符规则的输入");
        }
    }

    //批量删除选择
    $(".all_delete_check").click(function () {
        var check = $(".all_delete_check").prop("checked");
        if (check) {
            $(".delete_check").prop("checked", true);
        } else {
            $(".delete_check").prop("checked", false);
        }
    });

    //批量删除事件
    function allDelete() {
        var batch = $(".delete_check:checked");
        var id_batch = [];
        batch.each(function (idx) {
            var tr = $(this).parents("tr");
            var id = tr.children("td:eq(0)").text();
            id_batch.push(id);
        });
        var id_batch_string = id_batch.join(",");
        var conf = confirm("确认删除？");
        if (conf) {
            $.post('auto_create_campaign/<%=network%>/delete', {
                id_batch: id_batch_string
            }, function (data) {
                if (data && data.ret == 1) {
                    admanager.showCommonDlg("成功", data.message);
                    setTimeout(function () {
                        $("#common_message_dialog").modal("hide");
                    }, 1500);
                    batch.each(function (idx) {
                        var tr = $(this).parents("tr");
                        tr.empty();
                    });
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, "json")
        }
    }

    //批量开启
    function allChecked() {
        var checked = $(".all_checkbox_campaign_enable").prop("checked");
        if (checked) {
            //全部“开启”后
            $(".checkbox_campaign_enable").prop("checked", true);
            var list = $("tr:gt(0)");
            var id_batch = "";
            for (var i = 0; i < list.length; i++) {
                var temp = $(list[i]);
                var id = temp.children("td").get(0).innerHTML;
                id_batch += id + ",";
            }
            id_batch = id_batch.replace(/(.*)(,)/, "$1");
            $.post("auto_create_campaign/<%=network%>/enable", {
                id_batch: id_batch,
                enable: checked
            }, function (data) {
                if (data.ret == 1) {
                    admanager.showCommonDlg("提示", data.message);
                } else {
                    admanager.showCommonDlg("提示", data.message);
                }
            });
        } else {
            //取消全部“开启”后
            $(".checkbox_campaign_enable").prop("checked", false);
            var list = $("tr:gt(0)");
            var id_batch = "";
            for (var i = 0; i < list.length; i++) {
                var temp = $(list[i]);
                var id = temp.children("td").get(0).innerHTML;
                id_batch += id + ",";
            }
            id_batch = id_batch.replace(/(.*)(,)/, "$1");
            $.post("auto_create_campaign/<%=network%>/enable", {
                id_batch: id_batch,
                enable: checked
            }, function (data) {
                if (data.ret == 1) {
                    admanager.showCommonDlg("提示", data.message);
                } else {
                    admanager.showCommonDlg("提示", data.message);
                }
            });
        }
    }

    // 点击[开启]checkbox后往后台修改表
    function bindOp() {
        $(".checkbox_campaign_enable").click(function () {
            var checkbox = $(this);
            if (checkbox.prop("checked") == false) {
                $(".all_checkbox_campaign_enable").prop("checked", false);
            }
            var tr = $(this).parents("tr");
            var tds = tr.find('td');
            var id = $(tds.get(0)).text();

            var checked = $(this).prop('checked');
            $.post('auto_create_campaign/<%=network%>/enable', {
                id: id,
                enable: checked,
            }, function (data) {
                if (data && data.ret == 1) {
                    admanager.showCommonDlg("成功", data.message);
                } else {
                    admanager.showCommonDlg("错误", data.message);
                }
            }, 'json');

        });
    }

    function setData(data) {
        for (var i = 0; i < data.length; i++) {
            var one = data[i];
            var tr = $('<tr></tr>');

            var td = $('<td></td>');
            td.text(one.id);
            tr.append(td);

            td = $('<td></td>');
            td.text(one.app_name);
            tr.append(td);

            td = $('<td></td>');
            td.text(one.account_id);
            tr.append(td);

            td = $('<td></td>');
            td.text(one.country_region);
            tr.append(td);

            <%
            if (!"adwords".equals(network)) {
            %>
            td = $('<td></td>');
            td.text(one.language);
            tr.append(td);
            td = $('<td></td>');
            td.text(one.gender);
            tr.append(td);
            <% }%>

            td = $('<td></td>');
            td.text(one.bugdet);
            tr.append(td);

            td = $('<td></td>');
            td.text(one.image_path);
            tr.append(td);

            <%
            if (!"adwords".equals(network)) {
            %>
            td = $('<td></td>');
            td.text(one.video_path);
            tr.append(td);

            td = $('<td></td>');
            td.text(one.publisher_platforms);
            tr.append(td);

            td = $('<td></td>');
            td.text(one.bid_strategy);
            tr.append(td);
            <% }%>

            td = $('<td></td>');
            td.text(one.bidding);
            tr.append(td);

            td = $('<td></td>');
            td.text(one.mode_type);
            tr.append(td);

            td =
                $('<td><a class="link_modify glyphicon glyphicon-pencil" target="_blank" ' +
                    'href="campaigns_modify.jsp?type=auto_create&network=<%=network%>&id=' + one.id +
                    '"></a>&nbsp;/&nbsp;<input type="checkbox" class="delete_check"></td>');
            tr.append(td);
            td = $('<td></td>');
            td.html('<input class="checkbox_campaign_enable" type="checkbox" ' + (one.enabled == 1 ? 'checked' : '') + ' />');
            tr.append(td);
            $('.table tbody').append(tr);
        }
        goToPage(1);
        $(".changePage").hide();
    }

    /**
     * @param now 当前页码
     * @param psize 一页显示的数量，在此函数内写死
     */
    function goToPage(now) {
        $("nav").empty();
        var psize = 50;
        var totalPage = 0;
        var num = $("tbody tr").length;  //得到总行数
        if ((num / psize) > parseInt(num / psize)) {
            totalPage = parseInt(num / psize) + 1;
        } else {
            totalPage = parseInt(num / psize);
        }
        var currentPage = now;
        var start = (currentPage - 1) * psize;  //开始显示的行数-1
        var end = currentPage * psize;  //结束行
        end = (end > num) ? num : end;
        var initRow = $("tbody tr");
        var startRow = initRow;
        if (start > 0) {
            var startRow = $("tbody tr:gt(" + start + ")");
        }
        var endRow = $("tbody tr:gt(" + end + ")");
        initRow.hide();
        startRow.show();
        endRow.hide();
        var tempStr = "";
        if (currentPage > 1) {
            tempStr += "<a href=\"#\" onClick=\"goToPage(" + (currentPage - 1) + ")\">上一页&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>"
            for (var j = 1; j <= totalPage; j++) {
                tempStr += "<a href=\"#\" onClick=\"goToPage(" + j + ")\">" + j + "&nbsp;&nbsp;&nbsp;</a>"
            }
        } else {
            tempStr += "上一页&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
            for (var j = 1; j <= totalPage; j++) {
                tempStr += "<a href=\"#\" onClick=\"goToPage(" + j + ")\">" + j + "&nbsp;&nbsp;&nbsp;</a>"
            }
        }
        if (currentPage < totalPage) {
            tempStr += "<a href=\"#\" onClick=\"goToPage(" + (currentPage + 1) + ")\">下一页&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>";
            for (var j = 1; j <= totalPage; j++) {
            }
        } else {
            tempStr += "  下一页&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
        }
        $("nav").append(tempStr);
    }

    bindOp();
</script>
<script src="js/interlaced-color-change.js"></script>
</body>
</html>
