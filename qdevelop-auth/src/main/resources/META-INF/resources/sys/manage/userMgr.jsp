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
						<i class="fa fa-table"></i> 用户管理
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
	<%-- 	<jsp:include page="../import-footer.jsp" flush="true" /><div class="control-sidebar-bg"></div>
 --%>
</body>
<jsp:include page="../../import/import-javascript.jsp" flush="true" />
<script src="<%=request.getContextPath()%>/svr/conf/multi_system_name"></script>
<script type="text/javascript">
    $(function () {
    	
    	/**
    	*	{
    	*		url:'',
    	*		columns:[{field:'EPP_NAME',title:'职位名称',sortable:false,width:180,hidden:false,align:'center'}]	
    	*	}
    	*/
    	var _status = {'0':'无效','1':'正常'};
    	var _sysName = {'admin':'管理系统'};
        var table = $("#example").easyGrid({
        	data:{index:'qd_auth_users_query_action',status:1,order:'uid desc'},
        	columns: [
                      { "field": "login_name",title:'登陆名','sortable':true}
                      ,{ "field": "user_name" ,title:'用户名','sortable':true}
                      ,{ "field": "dept_name" ,title:'所属部门','sortable':false}
                      ,{ "field":"status",title:'状态','sortable':true,"formatter": function ( data, type, rowData, meta ) {
                          return  _status[data];  
                      }}
                      ,{ "field":"update_time",title:'更新时间','sortable':true}
                      ,{ "field":"last_login_time",title:'上次登陆','sortable':true}
                      ,{ "field":"login_times",title:'次数','sortable':true}
                  ],
              buttons:[
        			{act:'insert',title:'增加用户',isNew:true,callback:function(rowData){
        				$("#userAdd").easyForm('showData',{index:'qd_auth_users_add_action'});
        			}}
        			,{act:'edit',title:'修改信息',callback:function(rowData){
        				rowData['index'] = 'qd_auth_users_store_action';
        				$("#myModal").easyForm('showData',rowData);
        			}}
        			,{act:'role',icon:'fa-users',title:'配置权限',callback:function(rowData){
        				rowData['index'] = 'qd_auth_user_permit';
        				rowData['oldPermit'] = rowData['permit_id'] ;
        				$("#roleSetPanel").easyForm('showData',rowData);
        			}}
        			,{act:'reset',icon:'fa-unlock-alt',title:'重置密码',callback:function(rowData){
        				rowData['index'] = 'qd_auth_users_reset_password';
        				$("#resetPass").easyForm('showData',rowData);
        			}}
                  ]
        	,search:{
        		'login_name':{title:'登陆名',width:'100px'}
        		,'status':{title:'状态',width:'100px','type':'select',options:_status}
        		,'update_time':{title:'更新时间',type:'date'}
        		,'last_login_time':{title:'登陆时间',type:'date'}
        		//,'utime':{title:'修改日期',type:'date'}
        	}
        });
    	
    	var _options = {'0':'停用','1':'正常','2':'暂停'};
    	
    	$("#myModal").easyForm({
    		columns:[
				{"title":'',"field":'uid',"type":'hidden'},
				//{"title":'登陆名',"field":'login_name',"type":'varchar'},
				{"title":'用户名',"field":'user_name',"type":'varchar'},
				{"title":'状态',"field":'status',"type":'select',options:_status}
    		]
    	,submit:function(data,form){
	    	ajaxSubmit(data,form,table);
    	}});
    	
    	
    	$("#roleSetPanel").easyForm({
    		columns:[{"title":'',"field":'uid',"type":'hidden'}
    		,{"title":'',"field":'oldPermit',"type":'hidden'}],
    		onInit:function(formBody){
				$.ajax({
					url:'<%=request.getContextPath()%>/rest/cn/qdevelop/auth/adb/qd_auth_permit_query_action',
					data : {status : 1,limit:1000},
					success : function(r) {
						//debug(r);
						if (r.tag == 0) {
							var rb = r.data.result;
							for (var i = 0; i < rb.length; i++) {
								formBody.append('<div class="checkbox" style="float:left;margin-right:20px;"><label>'
								+'<input id="permit_id" type="checkbox" value="'+rb[i]['permit_id']+'">'
								+ rb[i]['permit_name']+ '</label></div>');
							}
						}
						}
					});
					},
					title : '权限设置',
					submit : function(data, form) {
						if (data.permit_id != data.oldPermit) {
							ajaxSubmit(data, form, table);
							//debug(data);
						} else {
							$(form).modal('hide');
						}
					}
				});

		var passReg = /^[a-zA-Z]\w{5,17}$/;
		function checkPassword(passwd) {
			if (!passReg.test(passwd)) {
				alert('密码必须为字母开头，由字母、数字组成，长度为6至18位');
				return false;
			}
			//passwd = passwd.MD5();
			return true;
		}

		$("#userAdd").easyForm({
			columns : [ {
				"title" : '登陆账号',
				"field" : 'login_name',
				"type" : 'text'
			}, {
				"title" : '登陆密码',
				"field" : 'login_passwd',
				"type" : 'password'
			}, {
				"title" : '用户实名',
				"field" : 'user_name',
				"type" : 'text'
			}, {
				"title" : '所属部门',
				"field" : 'dept_id',
				"type" : 'text'
			}
			// ,{"title":'',"field":'user_name',"type":'hidden'}
			//,{"title":'',"field":'user_name',"type":'hidden'}
			//,{"title":'',"field":'user_name',"type":'hidden'}
			//,{" title":'',"field":'user_name',"type":'hidden'}
			],
			title : '新增用户信息',
			submit : function(data, form) {
				if (typeof data['login_passwd'] != 'undefined') {
					if (checkPassword(data['login_passwd'])) {
						data['login_passwd'] = data['login_passwd'].MD5();
						ajaxSubmit(data, form, table);
					}
				}
			}
		});
		$("#resetPass").easyForm({
			title : '重置密码',
			columns : [ {
				"title" : '',
				"field" : 'uid',
				"type" : 'hidden'
			},{
				"title" : '登陆账号',
				"field" : 'login_name',
				"type" : 'text'
			},{
				"title" : '登陆密码',
				"field" : 'login_passwd',
				"type" : 'password'
			} ]
		,submit : function(data, form) {
			if (typeof data['login_passwd'] != 'undefined') {
				if (checkPassword(data['login_passwd'])) {
					data['login_passwd'] = data['login_passwd'].MD5();
					ajaxSubmit(data, form, table);
				}
			}
		}
		});
	});
</script>