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
						<i class="fa fa-table"></i> 菜单管理
					</h3>
				</div>
				<div class="box-body">
					<table id="example"
						class="table table-striped table-bordered table-hover"
						cellspacing="0" width="100%">
					</table>
				</div>
			</div>
		</section>
	</div>
	
	<div class="modal fade" id="roleSetPanel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">  
		    <div class="modal-dialog" role="document">  
		        <div class="modal-content">  
		            <div class="modal-header">  
		                <button type="button" class="close" data-dismiss="modal" aria-label="Close">  
		                    <span aria-hidden="true">×</span>  
		                </button>  
		                <h4 class="modal-title" id="myModalLabel">我的表单</h4>  
		            </div>  
		            <div class="modal-body form-horizontal">  
		            	<div class="nav-tabs-custom">
							<ul class="nav nav-tabs">
							        <li class="active"><a href="#tab_1" data-toggle="tab" aria-expanded="true">指定角色设定</a></li>
							        <li class=""><a href="#tab_2" data-toggle="tab" aria-expanded="false">指定用户设定</a></li>
							</ul>
							<div class="tab-content">
							        <div class="tab-pane active" id="tab_1">
							             <div  class="form-group row" id="roleContent" style="padding-left:15px"></div>
							        </div>
							        <!-- /.tab-pane -->
							        <div class="tab-pane" id="tab_2">
										<div class="form-group row">
			            					<div class="col-sm-10"><input class="form-control" id="userSearch" placeholder="搜索用户名或部门名，模糊匹配用*代替"/></div>
			            					<div class="col-sm-2"><button id="userSearchClick" class="btn btn-info btn-flat">搜索</button></div>
						            	</div>
						            	<div class="form-group row">
						            		<div class="col-sm-6">
						            		待选择用户列表：
							            	<select id="searchUsers" multiple="multiple" class="form-control" style="height:150px">
							            		
							            	</select>
							            	<span>双击选择该用户</span>
							            	</div><div class="col-sm-6">
							            	已设定权限独立用户：
							            	<select  id="selectedUsers" multiple="multiple" class="form-control" style="height:150px">
							            		
							            	</select>
							            	<span>双击移除该用户</span>
							            	</div>
						            	</div>
						            	
							        </div>
							        <!-- /.tab-pane -->
							</div>
							<!-- /.tab-content -->
						</div>
		            	
		            	<!-- <fieldset>
   						 	<legend>指定用户权限</legend>
			            	
		            	</fieldset> -->
		            </div>  
		            <div class="modal-footer">  
		                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>  
		                <button type="button" class="btn btn-primary">提交</button>  
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
        var table = $("#example").easyGrid({
        	data:{index:'qd_auth_menu_query_action',status:1,order:'sys_name,parent_id desc,sort desc'},
        	columns: [
					{ "field": "menu_id" ,title:'ID','sortable':true}
					   ,{ "field": "parent_id",title:'父菜单','sortable':true,'createdCell':function (td, cellData, rowData, row, col) {
	                    	  $(td).html('<span class="fa fa-fw '+cellData+'"></span><span>'+rowData['parent_menu_name']+'</span>');
	                      }}
                      ,{ "field": "menu_name" ,title:'菜单名','sortable':true,'createdCell':function (td, cellData, rowData, row, col) {
                    	  $(td).html('<span class="fa fa-fw '+rowData['menu_icon']+'"></span><span>'+rowData['menu_name']+'</span>');
                      }}
                      ,{ "field": "menu_link" ,title:'访问链接','sortable':true}
                      ,{ "field":"sys_name",title:'所属系统','sortable':true}
                      ,{ "field":"status",title:'状态','sortable':true}
                      ,{ "field":"update_time",title:'更新时间','sortable':true}
                  ],
              buttons:[
        			{act:'insert',title:'增加菜单',isNew:true,callback:function(rowData){
        				$("#myModal").easyForm('showData',{index:'qd_auth_menu_add_action',status:1,sys_name:multi_system_name[0],sort:0});
        			}}
        			/* ,{act:'menuPermit',title:'批量权限',isNew:true,callback:function(rowData){
        				//$("#myModal").easyForm('showData',{index:'qd_auth_menu_add_action'});
        			}} */
        			,{act:'edit',title:'修改菜单',callback:function(rowData){
        				rowData['index'] = 'qd_auth_menu_store_action';
        				$("#myModal").easyForm('showData',rowData);
        			}}
        			,{act:'role',icon:'fa-users',title:'配置权限',callback:function(rowData){
        				rowData['index'] = 'qd_auth_menu_permit_menu_role_set';
        				//rowData['oldPermit'] = rowData['permit_id'] ;
        				$("#roleSetPanel").easyForm('showData',rowData);
        			}}
                  ]
        	,search:{
        		'menu_name':{title:'菜单名',width:'100px'}
        		,'menu_link':{title:'访问链接',width:'100px'}
        		,'parent_id':{title:'父菜单',width:'110px','type':'select',ajax:{index:'qd_auth_menu_simple',parent_id:0,limit:1000,page:1},text:'menu_name',value:'menu_id'}
        		,'sys_name':{title:'系统',width:'110px','type':'select',options:multi_system_name}
        		,'status':{title:'状态',width:'100px','type':'select',options:qd_auth_status}
        		,'update_time':{title:'更新时间',type:'date'}
        		//,'utime':{title:'修改日期',type:'date'}
        	}
        });
    	    	
    	$("#myModal").easyForm({
    		title:'菜单设置',
    		columns:[
				{"title":'',"field":'menu_id',"type":'hidden'},
				//{"title":'登陆名',"field":'login_name',"type":'varchar'},
				{"title":'图标名',"field":'menu_icon',"type":'varchar'},
				{"title":'菜单名',"field":'menu_name',"type":'varchar'},
				{"title":'访问链接',"field":'menu_link',"type":'varchar'},
				{"title":'上级菜单',"field":'parent_id',"type":'select',options:multi_system_name},
				{"title":'排序',"field":'sort',"type":'int'},
				{"title":'所属系统',"field":'sys_name',"type":'select',options:multi_system_name},
				{"title":'状态',"field":'status',"type":'select',options:qd_auth_status}
    		]
    	,onShow:function(formBody,rowData){
    		//alert(1);
    		$.ajax({
				url:'<%=request.getContextPath()%>/rest/cn/qdevelop/auth/adb/qd_auth_menu_simple',
				data : {parent_id : 0,limit:1000},
				success : function(r) {
					if (r.tag == 0 ) {
						$('#parent_id',formBody).html('');
						var parent_id_select = $('#parent_id',formBody).get(0);
						parent_id_select.options.add(new Option('根菜单','0'));
						var rb = r.data.result;
						console.log(parent_id_select)
						for (var i = 0; i < rb.length; i++) {
							parent_id_select.options.add(new Option(rb[i]['menu_name'],rb[i]['menu_id']));
						}
						if(typeof rowData['parent_id'] != 'undefined'){
							for(var i=0;i<parent_id_select.options.length;i++){
								if(rowData['parent_id'] == parent_id_select.options[i].value){
									parent_id_select.options[i].selected = 'selected';
									break;
								}
							}
						}
					}
				}
			});
    	}
    	,submit:function(data,form){
	    	ajaxSubmit(data,form,table);
    	}});
    	
    	$("#roleSetPanel").easyForm({
    		columns:[{"title":'',"field":'uid',"type":'hidden'}
    		,{"title":'',"field":'menu_id',"type":'hidden'}],
    		onInit:function(formBody){
			
			}
			,onShow:function(formBody,rowData){
				$.ajax({
					url:'<%=request.getContextPath()%>/sys/menu/getRoleSetByMenuId',
					data : {menu_id : rowData['menu_id'],limit:1000},
					//async:false,
					success : function(r) {
						if (r.tag == 0 ) {
							var panel = $('#roleContent',formBody);
							panel.html('');
							var rb = r.data.permitList;
							for (var i = 0; i < rb.length; i++) {
								panel.append('<div class="checkbox" style="float:left;margin-right:20px;"><label>'
								+'<input id="permit_id" type="checkbox" value="'+rb[i]['permit_id']+'" '+(rb[i]['isChecked']?'checked="checked"':'')+'>'
								+ rb[i]['permit_name']+ '</label></div>');
							}
							
							if(typeof r.data.usersList != 'undefined'){
								rb = r.data.usersList;
								var html = '';
								for (var i = 0; i < rb.length; i++) {
									html += '<option value="'+rb[i]['uid']+'">'+rb[i]['user_name']+'</option>'
								}
								$('#selectedUsers',formBody).html(html);
							}else{
								$('#selectedUsers',formBody).html('');
							}
						}
					}
				});
			}
			,title : '菜单权限设置',
			submit : function(data, form) {
				if(typeof data['permit_id'] == 'undefined'){
					data['permit_id'] = '0';
				}
				var options = document.getElementById("selectedUsers").options;
				for(var i=0;i<options.length;i++){
					if(typeof data['uid'] == 'undefined'){
						data['uid'] = options[i].value;
					}else{
						data['uid'] = data['uid']+";"+options[i].value;
					}
				}
				if(typeof data['uid'] == 'undefined'){
					data['uid'] = '0';
				}
				ajaxSubmit(data, form);
			}
		});
		$('#userSearchClick').click(function(){
			var searchContent = $('#userSearch').val();
			if(searchContent!=null && searchContent.length > 0){
				searchContent = searchContent.indexOf('*')>-1?searchContent:'*'+searchContent+'*';
				$.ajax({
					url:'<%=request.getContextPath()%>/rest/cn/qdevelop/auth/adb/qd_auth_users_simple',
					data : {'user_name':searchContent,limit:1000},
					success : function(r) {
						if (r.tag == 0) {
							var rb = r.data.result;
							var html = '';
							for (var i = 0; i < rb.length; i++) {
								html += '<option value="'+rb[i]['uid']+'">'+rb[i]['user_name']+'</option>'
							}
							$('#searchUsers').html(html);
						}else{
							alert(r.errMsg);
						}
					}
				});
			}else{
				alert('请输入超过1个字符的查询条件');
			}
		});
		
		$('#searchUsers').dblclick(function(){
			var curData = this[this.selectedIndex];
    		//this.remove(this.selectedIndex);
    		document.getElementById("selectedUsers").options.add(new Option(curData.text,curData.value));
		});
		
		$('#selectedUsers').dblclick(function(){
			var curData = this[this.selectedIndex];
    		this.remove(this.selectedIndex);
		});
		
	});
</script>