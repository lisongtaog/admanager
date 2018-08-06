<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

    <c:set var="ctx" value="${pageContext.request.contextPath}"/>
    <%--版本号，防止css、js缓存；定义此版本号--%>
    <c:set var="version" value="1.0"/>

    <%--最根本的jsp页面，用于定义引入基本的js和css--%>
    <script src="${ctx}/common/statics/js/jquery-3.3.1.min.js"></script>
    <script src="${ctx}/common/statics/js/jquery-ui/jquery-ui.min.js"></script>
    <link rel="stylesheet" href="${ctx}/common/statics/js/jquery-ui/jquery-ui.min.css"/>
    <link rel="stylesheet" href="${ctx}/common/statics/js/jquery-ui/jquery-ui.theme.min.css"/>
    <link rel="stylesheet" href="${ctx}/common/statics/js/jquery-ui/jquery-ui.structure.min.css"/>
    <link rel="stylesheet" href="${ctx}/common/statics/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="${ctx}/common/statics/bootstrap/css/bootstrap-theme.min.css"/>
    <link rel="stylesheet" href="${ctx}/common/statics/css/bootstrap-tagsinput.css"/>
    <link rel="stylesheet" href="${ctx}/common/statics/js/datatables/datatables.min.css" />
    <link rel="stylesheet" href="${ctx}/common/statics/css/bootstrap-datetimepicker.css"/>
    <link rel="stylesheet" href="${ctx}/common/statics/bootstrap-select/bootstrap-select.min.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.4/css/select2.min.css" rel="stylesheet" />
    <link rel="stylesheet" href="${ctx}/common/statics/css/core.css"/>
    <link rel="stylesheet" href="${ctx}/common/statics/css/base.css?v=${version}" />


    <script src="${ctx}/common/statics/js/jquery-validation/jquery.validate.min.js"></script>
    <script src="${ctx}/common/statics/js/jquery.form.js"></script>
    <script src="${ctx}/common/statics/js/datatables/datatables.min.js"></script>
    <script src="${ctx}/common/statics/bootstrap/js/bootstrap.min.js"></script>
    <script src="${ctx}/common/statics/bootstrap/js/bootstrap-datetimepicker.js"></script>
    <%--<script src="${ctx}/common/statics/bootstrap/js/locales/bootstrap-datetimepicker.zh-CN.js"></script>--%>
    <script src="${ctx}/common/statics/bootstrap-select/bootstrap-select.min.js"></script>
    <script src="${ctx}/common/statics/bootstrap-select/i18n/defaults-en_US.js"></script>


    <script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.4/js/select2.min.js"></script>
    <script src="${ctx}/common/statics/js/layer/layer.js" ></script>
    <script src="${ctx}/common/statics/js/core.js"></script>
    <script type="text/javascript" src="${ctx}/js/language-name-code-dict.js"></script>
    <script src="${ctx}/js/country-name-code-dict.js"></script>

    <script type="text/javascript">
        var contextRootPath = "${ctx}";
        var ResponseCode = {
            "success":"0000",
            "error":"9999"
        }

        //操作类型
        var Operate = {
            "view":0,//查看详情
            "edit":1 //编辑修改
        }
        $.fn.serializeObject = function()
        {
            var o = {};
            var a = this.serializeArray();
            $.each(a, function() {
                if (o[this.name] !== undefined) {
                    if (!o[this.name].push) {
                        o[this.name] = [o[this.name]];
                    }
                    o[this.name].push(this.value || '');
                } else {
                    o[this.name] = this.value || '';
                }
            });
            return o;
        };
        
        
        function  AJAXerror (XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest.status);// 状态码
            console.log(XMLHttpRequest.readyState);// 状态
            console.log(textStatus);// 错误信息
        }

        /**
         * 获取指定日期字符串,YYYY-MM-DD格式
         * @param dateTime 不传，则默认取系统当前时间
         * @returns {string}
         */
        function getDateStr(dateTime) {
            if(!dateTime || !(dateTime instanceof Date) ){
                dateTime = new Date();
            }
            var month = dateTime.getMonth() + 1;
            var date = dateTime.getDate();
            month = month < 10 ? "0"+month : month;
            date = date < 10 ? "0" + date : date;

            return dateTime.getFullYear() + "-" + month + "-" + date;
        }

        //jquery datatable 异常事件处理；取消弹窗警告
        //开发过程中 可以放开进行调试
        $.fn.dataTable.ext.errMode = 'none';

    </script>
