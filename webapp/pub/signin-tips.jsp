<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<div class="form-info" style="height: 270px; margin-top: 50px">
	<h4>访问提示</h4>
	<p>建议使用最新版本Firefox浏览器访问应用以避免不必要的浏览器兼容性问题。</p>
	
	<s:if test="%{devMode||demoMode}">
		<p id="devModeTips" style="padding: 10px">
			<b> 开发/测试/演示登录快速入口: <a href="javascript:void(0)" class="" onclick="setupDevUser('admin','admin')">admin</a>
			</b>
		</p>
		<script type="text/javascript">
            function setupDevUser(user, password) {
                var $form = $("#login-form");
                $("input[name='j_username']", $form).val(user);
                $("input[name='j_password']", $form).val(password);
                $("input[name='j_captcha']", $form).val('admin');
                $form.submit();
            }
            jQuery(document).ready(function() {
                $("#devModeTips").pulsate({
                    color : "#bf1c56",
                    repeat : 10
                });
            });
        </script>
	</s:if>
</div>