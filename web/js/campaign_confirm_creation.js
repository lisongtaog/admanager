/**
 * 该文件原属于 campaign_create.js 的功能，用于创建前确认，但暂时不用了（2018-5-28 Xixi）
 */
var confirmModal = {};
confirmModal.showCampaignConfirm = function(params){
    $("#campaign_confirm_dialog").modal("show");
    $("#campaign_confirm_table_body").empty();
    var mark = 0;
    //在弹出窗口里生成表格
    params.forEach(function(param){
        var specialTr = $("<tr class='info' id = 'poolIndex"+mark+"'></tr>");
        var hideOrShow = $("<td></td>");
        var moreInfo = $("<button class='btn btn-link glyphicon glyphicon-plus'></button>");
        hideOrShow.append(moreInfo);
        var campaign = $("<td>"+param["campaignName"]+"</td>");
        campaign.on("click",function(){
            var td = $(this);
            if(td.children("input").attr("class")==="new_campaign_name"){
                return false;
            }
            var text = td.text();
            td.empty().append("<input class='new_campaign_name' type='text' style='width: 100%' value='"+text+"'>");
        });
        specialTr.append(moreInfo,campaign);
        $("#campaign_confirm_table_body").append(specialTr);
        for(var key in param){
            if(key ==="adsGroup" || key === "identification" || key === "campaignName"){
                continue;
            }else{
                var tr = $("<tr class='row"+ mark.toString()+ "'></tr>");  //用于操纵 DOM元素
                var keyTd = $("<td></td>").text(key);
                var valueTd = $("<td></td>").text(param[key]);
                tr.append(keyTd,valueTd);
                $("#campaign_confirm_table_body").append(tr);
            }
        }
        var idtf = mark.toString();
        moreInfo.click(function(){   //在 $("."+mark) 创建以后才绑定事件
            // var idtf = mark.toString(); //我原本是这样写的，但是每次点击，发现idtf会变成比循环完毕的mark多一次但为什么啊？
            var button = $(this);
            if(button.hasClass("glyphicon-plus")){
                $(".row"+ idtf).show();
                button.removeClass("glyphicon-plus").addClass("glyphicon-minus");
            }else{
                $(".row"+ idtf).hide();
                button.removeClass("glyphicon-minus").addClass("glyphicon-plus");
            }
        });
        $(".row"+ idtf).hide();
        mark++;
    });
};