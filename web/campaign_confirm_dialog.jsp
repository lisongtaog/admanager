<%--
  Created by IntelliJ IDEA.
  User: Xixi
  Date: 2018/5/24
  Time: 15:03
  To change this template use File | Settings | File Templates.
 * 该文件原属于 campaign_create.js 的功能，用于创建前确认，但暂时不用了（2018-5-28 Xixi）
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>

<div  id="campaign_confirm_dialog" class="modal fade model-large" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-large" role="document">
       <div class="modal-content">   <!-- 该层用于显示背景版 -->
           <div class="modal-header">
               <button type="button" class="close" data-dismiss="modal" aria-hidden="true"><span aria-hidden="true">&times;</span></button>
               <h4>信息确认</h4>
           </div>
           <div class="modal-body">
               <table class="table table-bordered">
                   <thead><th>显示/隐藏</th><th>系列信息</th></thead>
                   <tbody id="campaign_confirm_table_body"></tbody>
               </table>
           </div>
           <div class="modal-footer">
               <p id = "confirmed_campaign_info"></p>
               <button id="confirmed_campaign_creation" type="button" class="btn btn-default glyphicon glyphicon-ok">创建</button>
           </div>
       </div>
    </div>
</div>
