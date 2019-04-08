<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="../../import/import-css.jsp" flush="true" />
<body class="hold-transition skin-blue sidebar-mini">
	 <jsp:include page="../../import/import-header.jsp" flush="true" /> 
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
		          <h3 class="box-title"><i class="fa fa-table"></i> 权限角色管理</h3>
		        </div>
		        <div class="box-body">
					<table id="example" class="table table-striped table-bordered table-hover" >
					</table>
				</div>
			</div>
		</section>
		
		<div class="modal fade" id="selfRoleSet" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">  
		    <div class="modal-dialog" role="document">  
		        <div class="modal-content">  
		            <div class="modal-header">  
		                <button type="button" class="close" data-dismiss="modal" aria-label="Close">  
		                    <span aria-hidden="true">×</span>  
		                </button>  
		                <h4 class="modal-title" id="myModalLabel">我的表单</h4>  
		            </div>  
		            <div class="modal-body form-horizontal">
		            	<div class="form-group row"> 
		            		<label class="col-sm-4 control-label">手工URI规则：</label> 
		            		<div class="col-sm-6"> <input type="text" class="form-control" placeholder="模糊匹配用*"> </div>
		            		<div class="col-sm-2"> <button id="addNewRole" class="btn btn-info btn-flat">新增</button> </div> 
		            	</div>  
		            	<hr/>  
		            	<div class="form-group row">
		            		<div class="col-sm-6">
			            	参考规则：
			            	</div><div class="col-sm-6">
			            	已选定规则：
			            	</div>
		            	</div>
		            	<div class="form-group row">
		            		<div class="col-sm-6">
				            	<select id="otherRule" multiple="multiple" class="form-control" size="10">
				            		<option>aaa</option>
				            		<option>bbb</option>
				            	</select>
			            	</div>
			            	<div class="col-sm-6">
				            	<select  id="selectedRule" multiple="multiple" class="form-control" size="10">
				            		<option>ccc</option>
				            		<option>ddd</option>
				            	</select>
			            	</div>
		            	</div>
		            	<div class="form-group row" style="font-color:#fa0733;text-align:center;">
		            		请双击规则内容，进行数据操作
		            	</div>
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
<script src="<%=request.getContextPath()%>/svr/conf/multi_system_name,qd_auth_status"></script>


<script type="text/javascript">
    $(function () {
    	
    	/**
    	*	{
    	*		url:'',
    	*		columns:[{field:'EPP_NAME',title:'职位名称',sortable:false,width:180,hidden:false,align:'center'}]	
    	*	}
    	*/
    	//var _status = {'0':'无效','1':'正常'};
        var table = $("#example").easyGrid({
        	data:{index:'qd_auth_permit_query_action',status:1,order:'permit_id desc'},
        	columns: [
                      { "field": "permit_name",title:'角色名','sortable':false}
                      ,{ "field": "sys_name" ,title:'所属系统','sortable':true,"formatter": function ( data, type, rowData, meta ) {
                          return  multi_system_name[data];  
                      }}
                      ,{ "field":"status",title:'状态','sortable':true,"formatter": function ( data, type, rowData, meta ) {
                          return  qd_auth_status[data];  
                      }}
                      ,{ "field":"update_time",title:'更新时间','sortable':true}
                  ],
              buttons:[
        			{act:'insert',title:'增加角色',isNew:true,callback:function(rowData){
        				$("#myModal").easyForm('showData',{index:'qd_auth_permit_add_action'});
        			}}
        			,{act:'edit',title:'修改角色',callback:function(rowData){
        				rowData['index'] = 'qd_auth_permit_store_action';
        				$("#myModal").easyForm('showData',rowData);
        			}}/*fa-registered*/
        			,{act:'role',icon:'fa-gears',title:'设置手工权限',callback:function(rowData){
        				rowData['index'] = 'qd_auth_menu_permit_self_save';
        				$("#selfRoleSet").easyForm('showData',rowData);
        			}}
                  ]
        	,search:{
        		'permit_name':{title:'角色名',width:'100px'}
        		,'status':{title:'状态',width:'100px','type':'select',options:qd_auth_status}
        		//,'ctime':{title:'创建日期',type:'date'}
        		,'update_time':{title:'修改日期',type:'date'}
        	}
        });
    	
    	var _options = {'0':'停用','1':'正常','2':'暂停'};
    	
    	$("#myModal").easyForm({
    		title:'角色管理表单',
    		columns:[
				{"title":'',"field":'permit_id',"type":'hidden'},
				{"title":'角色名',"field":'permit_name',"type":'varchar'},
				{"title":'系统',"field":'sys_name',"type":'select',options:multi_system_name},
				{"title":'状态',"field":'status',"type":'select',options:qd_auth_status}
    		]
    	,submit:function(data,form){
    		ajaxSubmit(data,form,table);
    	}});
    	
    	$('#selfRoleSet').easyForm({
    		title:'设置手工权限',
    		columns:[{"title":'',"field":'permit_id',"type":'hidden'}
    		,{"title":'',"field":'oldPermit',"type":'hidden'}],
    		onShow:function(formBody,rowData){
    			$('#otherRule',formBody).empty();
    			$('#selectedRule',formBody).empty();
    			$.ajax({
					url:'<%=request.getContextPath()%>/rest/cn/qdevelop/auth/adb/qd_auth_menu_permit_self_control',
					data : {permit_id : rowData['permit_id'],limit:1000},
					success : function(r) {
						if (r.tag == 0) {
							var rb = r.data.result;
							for (var i = 0; i < rb.length; i++) {
								document.getElementById("selectedRule").options.add(new Option(rb[i]['permit_link'],rb[i]['permit_link']));
							}
						}
					}
				});
    			$.ajax({
					url:'<%=request.getContextPath()%>/rest/cn/qdevelop/auth/adb/qd_auth_menu_permit_self_control',
					data : {permit_id : '!'+rowData['permit_id'],limit:1000},
					success : function(r) {
						if (r.tag == 0) {
							var rb = r.data.result;
							for (var i = 0; i < rb.length; i++) {
								document.getElementById("otherRule").options.add(new Option(rb[i]['permit_link'],rb[i]['permit_link']));
							}
						}
					}
				});
    		},
    		onInit:function(formBody){
    			
    		},submit:function(data,form){
    			//console.log(data);
    			var options = document.getElementById("selectedRule").options;
    			for(var i=0;i<options.length;i++){
    				//console.log(">> "+options[i].value);
    				if( typeof data['permit_link'] == 'undefined'){
    					data['permit_link'] = options[i].value;
    				}else{
    					data['permit_link'] += "^"+options[i].value;
    				} 
    			}
    			if(options.length > 0){
    				$.ajax({
    					url:'<%=request.getContextPath()%>/rest/cn/qdevelop/auth/adb/qd_auth_menu_permit_self_save',
    					data : data,
    					success : function(r) {
    						if (r.tag == 0) {
    							$(form).modal('hide');
    							alert('设置手工权限成功！');
    							table.ajax.reload();
    						}else{
    							alert(r.errMsg);
    						}
    					}
    				});
    			}
    			
    		}
    	});
    	
    	$('#otherRule').dblclick(function(){
    		var curData = this[this.selectedIndex];
    		//console.log(curData.innerText);
    		this.remove(this.selectedIndex);
    		document.getElementById("selectedRule").options.add(new Option(curData.innerText,curData.innerText));
    	});
    	$('#selectedRule').dblclick(function(){
    		var curData = this[this.selectedIndex];
    		//console.log(curData.innerText);
    		this.remove(this.selectedIndex);
    		document.getElementById("otherRule").options.add(new Option(curData.innerText,curData.innerText));
    	});
    	$('#addNewRole').click(function(){
    		var input = $(this).parent().prev().children(':input');
    		if(input.val().length > 0 ){
    			document.getElementById("selectedRule").options.add(new Option(input.val(),input.val()));
    			input.val('');
    		}
    	});
    	
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