<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="../../import/import-css.jsp" flush="true" />
<body class="hold-transition skin-blue sidebar-mini">
<%-- 	 <jsp:include page="../import-header.jsp" flush="true" /> 
 --%>	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<!-- <section class="content-header">
			<h1>
				<small>日志查询</small>
			</h1>
			<ol class="breadcrumb">
				<li><a href="#"><i class="fa fa-dashboard"></i> 主页</a></li>
				<li class="active">表格测试</li>
			</ol>
		</section> -->

		<!-- Main content -->
		<section class="content">
			<div class="box box-default color-palette-box">
		        <div class="box-header with-border table-bordered">
		          <h3 class="box-title"><i class="fa fa-table"></i> 日志查询</h3>
		        </div>
		        <div class="box-body">
					<table id="example" class="table table-striped table-bordered table-hover" cellspacing="0" width="100%">
						
					</table>
				</div>
			</div>
		</section>
		
		<!-- <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">  
		    <div class="modal-dialog" role="document">  
		        <div class="modal-content">  
		            <div class="modal-header">  
		                <button type="button" class="close" data-dismiss="modal" aria-label="Close">  
		                    <span aria-hidden="true">×</span>  
		                </button>  
		                <h4 class="modal-title" id="myModalLabel">我的表单</h4>  
		            </div>  
		            <div class="modal-body form-horizontal">  
		            	<div>test</div>
		                
		            </div>  
		            <div class="modal-footer">  
		                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>  
		                <button type="button" class="btn btn-primary">提交</button>  
		            </div>  
		        </div>  
		    </div>  
		</div> --> 
	</div>
<%-- 	<jsp:include page="../import-footer.jsp" flush="true" />
 --%>	<div class="control-sidebar-bg"></div>
</body>
<%-- <jsp:include page="<%=request.getContextPath()%>/import/import-javascript.jsp" flush="true" />
 --%><%-- <script type="text/javascript" src="<%=request.getContextPath()%>/svr/conf/ep_cash_amount_apply_status_map"></script>
 --%>
<script type="text/javascript">
    $(function () {
    	
    	/**
    	*	{
    	*		url:'',
    	*		columns:[{field:'EPP_NAME',title:'职位名称',sortable:false,width:180,hidden:false,align:'center'}]	
    	*	}
    	*/
        var table = $("#example").easyGrid({
        	data:{index:'simple_product_log_query_v3.0.0'},
        	columns: [
                      { "field": "user_name",title:'用户','sortable':false}
                      ,{ "field": "mobile" ,title:'手机','sortable':false}
                      ,{ "field": "pid" ,title:'PID','sortable':true}
                      ,{ "field": "product_name",title:'商品名',hidden:false}
                      ,{ "field":"action",title:'状态','sortable':true,hidden:false}
                      ,{ "field":"ctime",title:'创建时间','sortable':true,hidden:false}
                  ]
        	,search:{
        		'product_name':{title:'商品名检索',width:'100px'},
        		//'status':{title:'状态',width:'100px','type':'select',options:[{text:'停用',value:0},{text:'正常',value:1},{text:'暂停',value:2}]},
        		//'ctime':{title:'创建日期',type:'date'},
        		'utime':{title:'创建日期',type:'date'}
        	}
        });
    	
    	var _options = {'0':'停用','1':'正常','2':'暂停'};
    	
    	$("#myModal").easyForm({
    		columns:[
				{"title":'',"field":'pid',"type":'hidden'},
				{"title":'商品名',"field":'product_name',"type":'varchar'},
				{"title":'价格',"field":'price',"type":'double'},
				{"title":'用户',"field":'uid',"type":'int'},
				{"title":'库存',"field":'store',"type":'number'},
				{"title":'date',"field":'date',"type":'date'},
				{"title":'dateRange',"field":'dateRange',"type":'dateRange'},
				{"title":'dateTimeRange',"field":'dateTimeRange',"type":'dateTimeRange'},
				{"title":'状态',"field":'status',"type":'select',options:_options}
    		]
    	,submit:function(data,form){
    		ajaxSubmit(data,form,table);
    	}});
    	
    	
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