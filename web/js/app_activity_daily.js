var endDate = $("#inputEndDate").val();
var startDate = $("#inputStartDate").val();
var tagName = $("input[id='inputSearch']").val();

//拆分日期
function gDate(datestr){
    var temp = datestr.split("-");
    var date = new Date(temp[0],temp[1],temp[2]);
    return date;
}

//绑定在 #btnSearch 键上的操作
$("#btnSearch").click(function(){
    $("#total_result").empty();
    var endDate = $("#inputEndDate").val();
    var startDate = $("#inputStartDate").val();
    var tagName = $("input[id='inputSearch']").val();
    var result_body = $("<table border=\"1\" cellspacing=\"0\" cellpadding=\"5\" style='border-collapse:separate'></table>");
    $("#results_body").empty(); //清空原有数据
    //用于动态回显查询到的日志
    $.post("app_activity_daily/search",{
        endDate: endDate,
        startDate:startDate,
        tagName:tagName
    },function(data){
//得到 startDate 和 endDate 之间所有的日期,并输出到表格头
        var startTime = gDate(startDate);
        var endTime = gDate(endDate);
        var dateBetween = null;
        var tr = $("<tr id='table_head'></tr>"); //创建一个空行
        tr.append("<td>日期</td>");

//该循环列出时间行(从后往前)
        var dateCount = 0;
        while((endTime.getTime()-startTime.getTime())>=0){
            var year = endTime.getFullYear();
            var month = endTime.getMonth().toString().length==1 ? "0"+endTime.getMonth().toString() : endTime.getMonth();
            var day = endTime.getDate().toString().length==1 ? "0"+endTime.getDate() : endTime.getDate();
            dateBetween = year+"-"+month+"-"+day;
            var th_date = "<td>" + dateBetween + "</td>";
            tr.append(th_date);
            endTime.setDate(endTime.getDate()-1);
            dateCount ++;   //与总天数相同
        }
        result_body.append(tr);

        //创建一个用于存放textarea的行
        var textarea_tr = $("<tr id='textarea_tr'></tr>");
        var daily_td =  $("<td style='vertical-align:center'>日志创建</td>");
        textarea_tr.append(daily_td);
        var contentArray = data["content_array"]; //得到手写日志的json数组
        for(var count=0;count<dateCount;count++){
            var handwrite_content = contentArray[count]["content"];
            var date_textarea_td = $("<td></td>");
            var text_input_tr = $("<tr></tr>");
            var text_input = $("<textarea id=\'inputContent"+count+"\' style='width:300px;height:100px'></textarea>")
            if(handwrite_content != "(今日没有记录)"){
                text_input.val(handwrite_content);
            }else{
                text_input.attr("placeholder",handwrite_content);
            }
            text_input_tr.append(text_input);
            var btn_tr = $("<tr></tr>");
            var delete_btn = $("<button id=\'deleteContent"+count+"\' class=\'del_btn btn-default\'>清空</button>");  //该按钮绑定清空操作
            var create_btn = $("<button id=\'createContent"+count+"\' class=\'cre_btn btn-default\'>更新</button>");  //该按钮绑定插入操作
            btn_tr.append(create_btn);
            btn_tr.append(delete_btn);
            //把 textarea 和 button 都塞进单元格里
            date_textarea_td.append(text_input_tr);
            date_textarea_td.append(btn_tr);
            textarea_tr.append(date_textarea_td);
        }
        result_body.append(textarea_tr);

        //创建国家信息单行表头
        var country_tr_head = $("<tr></tr>");
        country_tr_head.append("<td>国家</td>");
        //在国家行添加日期表头
        for(var j=0;j<dateCount;j++){
            country_tr_head.append("<td>应用日活动记录</td>");
        }
        result_body.append(country_tr_head);

        //填充国家行（包括各天下的系列ID和系统操作）
        var countryArray = data["country_array"]; //得到各国家系统操作日志
        if(countryArray.length>0){
            for(var i=0;i<countryArray.length;i++){
                var one = countryArray[i];  //array[i]
                var country_tr = $("<tr></tr>"); //一个国家建一行
                var country = one["country_name"];
                var countryName = $("<td>"+country+"</td>");
                country_tr.append(countryName);
                var date_data = one["date_data"];  //得到arrayMiddle数组

                //以日期作循环填充日志单元格（从endDate到startDate）
                for(var j=0;j<date_data.length-1;j++){
                    var date_td = $("<td style='empty-cells: show'></td>"); //一个日期创一格
                    var two = date_data[j];   //得到arrayMiddle[j],该数组的长度实际比天数还多一天
                    var campaign_data = two["campaign_data"+j]; //直接得到拼接字符串内容
                    var statusData = two["status"+j];
                    var yesterdayStatusData = date_data[j+1]["status"+(j+1)];
                    var openOrCreate = two["exist_open_create"+j];

                    //如果当天处于系列全关状态
                    if(statusData == 2) {
                        statusData = "#ce8483";   //某种红色
                        date_td.css("background",statusData);
                    }
                    //如果昨天处于系列全关，而当天有新建或开启操作
                    if(yesterdayStatusData === 2 && openOrCreate === 1){
                        statusData = "#b2dba1";
                        date_td.css("background",statusData); //某种绿色
                    }
                    date_td.append(campaign_data);
                    country_tr.append(date_td);
                }
                result_body.append(country_tr); //每个国家行插入完毕后，添加到result_body里
            }
            $("#results_body").append(result_body); //所有国家行插完后，一个整体的DOM元素 result_body 被插入到html原本就有的DOM元素内
        }
        },"json");
});

/* 事件委托：事件绑定到父元素，等事件触发后再寻找相匹配的子元素
 * 坑：注意父元素绑定——在JS执行以前的 DOM元素上绑定，才能不受动态元素的影响
 */
$("#results_body").on('click','.del_btn',function(){
    var this_id = $(this).attr("id");
    var del_trim = this_id.replace(/deleteContent/,"");
    $("#inputContent"+del_trim).val(""); //清空当前按钮对应的textarea
});

//更新数据库表web_ad_write_app_daily_record的操作
$("#results_body").on('click','.cre_btn',function(){
    var this_id = $(this).attr("id");
    var cre_trim = this_id.replace(/createContent/,"");
    var creation = $("#inputContent"+cre_trim).val();
    var msg = "确认添加日志？"
    if(confirm(msg) === true){
        //日期的处理
        var this_date = endDate.split("-"); //分割日期字符串
        var theDate = new Date(Number(this_date['0']),Number(this_date['1']),Number(this_date['2']));
        theDate.setDate(theDate.getDate()-cre_trim); //得到新日期Date类型
        var year = theDate.getFullYear();
        var month = theDate.getMonth()<9 ? "0"+ theDate.getMonth() : ""+ theDate.getMonth();
        var day = theDate.getDate()<10 ? "0"+(theDate.getDate()) : ""+(theDate.getDate());
        this_date = year +"-"+ month + "-" + day;
        var tagName = $("input[id='inputSearch']").val();
        $.post("app_activity_daily/create",{
            content:creation ,
            this_date:this_date,
            tagName:tagName
        },function(data){  //这里的data是一个信息
            alert(data["message"]);
        },"json")
    }
});