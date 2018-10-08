<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="../../import/import-css.jsp" flush="true" />
<body class="hold-transition skin-blue sidebar-mini">
 <jsp:include page="../../import/import-header.jsp" flush="true" /> 
	<div class="content-wrapper">
		<section class="content">
			<div class="box box-default color-palette-box">
				<div class="box-header with-border table-bordered">
					<h3 class="box-title">
						<i class="fa fa-table"></i> 权限配置管理
					</h3>
				</div>
				<div class="box-body">
					<div id="treeDataView"></div>
				</div>
			</div>
		</section>
	</div>
	<%-- 	<jsp:include page="../import-footer.jsp" flush="true" /><div class="control-sidebar-bg"></div>
 --%>
</body>
<jsp:include page="../../import/import-javascript.jsp" flush="true" />

<script type="text/javascript">
	$(function() {
		
		
	});
</script>