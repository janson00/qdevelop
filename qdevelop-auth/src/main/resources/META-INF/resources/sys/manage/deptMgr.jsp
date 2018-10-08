<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="../../import/import-css.jsp" flush="true" />
<body class="hold-transition skin-blue sidebar-mini">
	<%-- 	 <jsp:include page="../import-header.jsp" flush="true" /> 
 --%>
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<!-- 	<section class="content-header">
			<h1>
				<small>表格测试</small>
			</h1>
			<ol class="breadcrumb">
				<li><a href="#"><i class="fa fa-dashboard"></i> 主页</a></li>
				<li class="active">表格测试</li>
			</ol>
		</section>
 -->
		<!-- Main content -->
		<section class="content">
			<div class="box box-default color-palette-box">
				<div class="box-header with-border table-bordered">
					<h3 class="box-title">
						<i class="fa fa-table"></i> 部门管理
					</h3>
				</div>
				<div class="box-body">
					<!-- <table id="example" class="table table-striped table-bordered table-hover" cellspacing="0" width="100%">
					</table> -->
					<div id="treeDataView"></div>
				</div>
			</div>
		</section>

		<div class="modal fade" id="myModal" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">×</span>
						</button>
						<h4 class="modal-title" id="myModalLabel">我的表单</h4>
					</div>
					<div class="modal-body form-horizontal">
						<!-- <div>test</div> -->

					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
						<button type="button" class="btn btn-primary">提交</button>
					</div>
				</div>
			</div>
		</div>
	</div>
	<%-- 	<jsp:include page="../import-footer.jsp" flush="true" /><div class="control-sidebar-bg"></div>
 --%>
</body>
<jsp:include page="../../import/import-javascript.jsp" flush="true" />

<script type="text/javascript">
	$(function() {
		var treeData = {"tag":0,"data":{"result":[{"child":[{"child":[{"dept_id":3,"dept_name":"java组","parent_id":2}],"dept_id":2,"dept_name":"姿美汇","parent_id":1}],"dept_id":1,"parent_id":0,"dept_name":"技术中心"}],"total":1,"limit":10,"page":1,"next":1},"errMsg":""};
		
		function treeHtmlCreate(data,dom){
			var content = $('<ul class="treeview"></ul>').appendTo(dom);
			for(var i=0;i<data.length;i++){
				var tree = data[i];
				content.append('<li><a href="javascript:void(0)">'+tree['dept_name']+'</a> <span>'
						+'&nbsp; <span class="fa fa-fw fa-plus" type="add" title="增加子部门"></span>'
						+'&nbsp; <span class="fa fa-fw fa-pencil"  type="edit" title="修改部门信息" ></span>'
						+'&nbsp; <span class="fa fa-fw fa-trash-o"  type="delete" title="删除该部门"></span>'
						+'</span></li>');
				if(typeof tree['child'] != 'undefined' ){
					var _dom = content.find('li');
					treeHtmlCreate(tree['child'],_dom);
				}
			}
		}

		//var dom = $('#treeDataView');
		treeHtmlCreate(treeData['data']['result'],$('#treeDataView'));
	
	$('#treeDataView').find('li').hover(function() {
			$(this).child('span').show();
		}, function() {
			$(this).child('span').hide();
		})
		/**
		 *	{
		 *		url:'',
		 *		columns:[{field:'EPP_NAME',title:'职位名称',sortable:false,width:180,hidden:false,align:'center'}]	
		 *	}
		 */
		/* 	var _status = {'0':'无效','1':'正常'};
			var _sysName = {'admin':'管理系统'};
		    var table = $("#example").easyGrid({
		    	data:{index:'qd_auth_dept_query_action',status:1,order:'permit_id desc'},
		    	columns: [
		                  { "field": "permit_name",title:'角色名','sortable':false}
		                  ,{ "field": "sys_name" ,title:'所属系统','sortable':true,"formatter": function ( data, type, rowData, meta ) {
		                      return  _sysName[data];  
		                  }}
		                  ,{ "field":"status",title:'状态','sortable':true,"formatter": function ( data, type, rowData, meta ) {
		                      return  _status[data];  
		                  }}
		                  ,{ "field":"update_time",title:'更新时间','sortable':true}
		              ],
		          buttons:[
		    			{act:'insert',title:'增加',callback:function(rowData){
		    				$("#myModal").easyForm('showData',{index:'qd_auth_permit_add_action'});
		    			}}
		    			,{act:'edit',title:'修改',callback:function(rowData){
		    				rowData['index'] = 'qd_auth_permit_store_action';
		    				$("#myModal").easyForm('showData',rowData);
		    			}}
		              ]
		    	,search:{
		    		'permit_name':{title:'角色名',width:'100px'}
		    		,'status':{title:'状态',width:'100px','type':'select',options:_status}
		    		//,'ctime':{title:'创建日期',type:'date'}
		    		//,'utime':{title:'修改日期',type:'date'}
		    	}
		    });
			
			var _options = {'0':'停用','1':'正常','2':'暂停'};
			
			$("#myModal").easyForm({
				columns:[
					{"title":'',"field":'permit_id',"type":'hidden'},
					{"title":'角色名',"field":'permit_name',"type":'varchar'},
					{"title":'系统',"field":'sys_name',"type":'select',options:_sysName},
					{"title":'状态',"field":'status',"type":'select',options:_status}
				]
			,submit:function(data,form){
				ajaxSubmit(data,form,table);
			}}); */

		//$('.modal-body',"#myModal").append('<div class="form-group"> <div class="col-sm-offset-2 col-sm-10"> <div class="checkbox"> <label> <input type="checkbox"> Remember me </label> </div> </div> </div>');
		//$('#myModal').modal('hide') ;
		/* $('#searchSubmit').click(function(){
			var args={};
			gridParams = table.context[0].oAjaxData.params;
			$(this).parent().children().each(function(){
				var val = $(this).val();
				var title=$(this).attr('title');
				if(typeof val == 'string' && typeof $(this).attr('id') == 'string' && val.indexOf(title) == -1 && val.length > 0){
					gridParams[$(this).attr('id')] = val;	
				}else{
					delete gridParams[$(this).attr('id')];
				}
			});
			table.ajax.reload();
		}); */

		/*  setTimeout( function () {
		   //手动 reload table
		    table.ajax.reload();
		    console.log(table.ajax);
		}, 5000 );   */
	});
</script>