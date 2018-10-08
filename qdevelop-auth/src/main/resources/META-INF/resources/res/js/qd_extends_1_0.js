;(function($){
	$.fn.datepicker.dates['zh-CN'] = {
		days: ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"],
		daysShort: ["周日", "周一", "周二", "周三", "周四", "周五", "周六"],
		daysMin:  ["日", "一", "二", "三", "四", "五", "六"],
		months: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
		monthsShort: ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"],
		today: "今日",
		clear: "清除",
		format: "yyyy年mm月dd日",
		titleFormat: "yyyy年mm月",
		weekStart: 1
	};
}(jQuery));

$(function () {
	$.fn.easyForm  = function(options,params){
		if(typeof options == "string"){
			$.fn.easyForm.method[options](this,params);
			return this;
		}
		//console.log(this.length);
		if(typeof this.length == 'undefined' || this.length == 0){
			//var id=this.selector;
			var id=this.selector.replace('#','');
			_target = $('<div class="modal fade" id="'+id+'" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"> <div class="modal-dialog" role="document"> <div class="modal-content"> <div class="modal-header"> <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true">×</span> </button> <h4 class="modal-title" id="myModalLabel">权限设置</h4> </div> <div id="formContent" class="modal-body form-horizontal"></div> <div class="modal-footer"> <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button> <button type="button" class="btn btn-primary">提交</button> </div> </div> </div> </div>');
			$('body').append(_target);
			_target = $(this.selector);
		}else{
			_target = this;
		}
		var settings = $.extend({
			title:'默认表单',
			columns:[
			 //  {"title":'',"field":'',type:'text'}
			],
			submit:function(data){
				return true;
			}
		},options);
		$('.modal-body',_target).append('<input type="hidden" id="index"/>');
		$('.modal-title',_target).html(settings.title);
		var dataModel = {};
		for(var i=0;i<settings.columns.length;i++){
			var formFileds = $.extend({"title":'',"field":'',"type":'text'},settings.columns[i]);
			dataModel[formFileds['field']] = '';
			var type = formFileds['type'].toUpperCase();
			var content = '';
			if(type == 'HIDDEN'){
				content += '<input type="hidden"  id="'+formFileds['field']+'" "/>';
			}else{
				content=' <div class="form-group"> <label for="'+formFileds['field']+'" class="col-sm-2 control-label">'+formFileds['title']+'：</label> <div class="col-sm-10">';
				switch(type){
					case 'PASSWORD' : 
						content += '<input type="'+formFileds['type']+'" class="form-control" id="'+formFileds['field']+'" placeholder="'+formFileds['title']+'"/>';
						break;
					case 'TEXTAREA' : 
						content += '<textarea class="form-control" id="'+formFileds['field']+'" placeholder="'+formFileds['title']+'"></textarea>';
						break;
					case 'SELECT' : 
						content += '<select class="form-control" id="'+formFileds['field']+'"></select>';
						break;
					case 'DATERANGE' : 
					case 'DATETIMERANGE' :
					case 'DATE' : 
						content += '<input class="form-control"  id="'+formFileds['field']+'" />';
						break;
					case 'HTML' : 
						content += '<span class="form-control"  id="'+formFileds['field']+'" ></span>';
						break;
					default:
						content += '<input type="'+formFileds['type']+'" class="form-control" id="'+formFileds['field']+'" "/>';
				}
				
				content += '</div> </div>';
			}
//			var node = $(content);
//			node.appendTo($('.modal-body',_target));
			$('.modal-body',_target).data('callback',settings['submit']).append(content);
			if(type == 'SELECT' && (typeof formFileds.options != 'undefined' || typeof formFileds.ajax != 'undefined')){
				$('#'+formFileds['field'],_target).selection(formFileds);
			}else if(type == 'DATE'){
				$('#'+formFileds['field'],_target).datepicker({ 
					format: 'yyyy-mm-dd',
					language:  "zh-CN",
					autoclose: true
				});
			}else if(type == 'DATERANGE'){
				$('#'+formFileds['field'],_target).daterangepicker({ 
					format: 'YYYY-MM-DD',
					language:  "zh-CN",
					locale:{  
                        applyLabel : '确定',  
                        cancelLabel : '取消',  
                        fromLabel : '起始时间',  
                        toLabel : '结束时间',  
                        customRangeLabel : '自定义日期',  
                        daysOfWeek : [ '日', '一', '二', '三', '四', '五', '六' ],  
                        monthNames : [ '一月', '二月', '三月', '四月', '五月', '六月','七月', '八月', '九月', '十月', '十一月', '十二月' ]  
                    } 
					//autoclose: true
				});
			}else if(type == 'DATETIMERANGE'){
				$('#'+formFileds['field'],_target).daterangepicker({ 
					format: 'YYYY-MM-DD h:mm A',
					language:  "zh-CN",
					timePicker: true,
					timePickerIncrement: 10,
					locale:{  
                        applyLabel : '确定',  
                        cancelLabel : '取消',  
                        fromLabel : '起始时间',  
                        toLabel : '结束时间',  
                        customRangeLabel : '自定义日期',  
                        daysOfWeek : [ '日', '一', '二', '三', '四', '五', '六' ],  
                        monthNames : [ '一月', '二月', '三月', '四月', '五月', '六月','七月', '八月', '九月', '十月', '十一月', '十二月' ]  
                    } 
					//autoclose: true
				});
			}
		}
		if(typeof settings.onInit == 'function'){
			settings.onInit($('.modal-body',_target));
		}
		if(typeof settings.onShow == 'function'){
			$(_target).data("onShow",settings.onShow);
		}
		$(_target).data("dataModel",dataModel);
		$(".btn-primary",_target).click(function(){
			var form = $(this).closest('.modal-footer').prev('.modal-body');
			//console.log(form.html());
			var datas = form.getData();
			//console.log(datas);
			if(typeof form.data('callback') == 'function'){
				isSucess = form.data('callback').call(this,datas,$(form).parent().parent().parent());
			}
			if(typeof isSucess != 'undefined' && isSucess){
				$(_target).modal('hide');
			}
		});
	};
	
	$.fn.easyForm.method = {
		'showData':function(target,option){
				var data = $.extend({},$(target).data('dataModel'),option);
				if(typeof $(target).data('onShow') == 'function'){
					$(target).data('onShow').call(this,target,data);
				}
				$(target).modal('show');
				$(target).setData(data);
		},
		'show':function(target,option){
			if(typeof $(target).data('onShow') == 'function'){
				$(target).data('onShow').call(this,target,{});
			}
			$(target).modal('show');
			$(target).setData($(target).data('dataModel'));
		},
		'hide':function(target,option){
			$(target).modal('hide');
		},
		'data':function(target,option){
			if(typeof option == 'object'){    				
				$('.modal-body',target).setData(option);
			}
		}
	};	
	/**
	*	{
	*		url:'',
	*		columns:[{field:'EPP_NAME',title:'职位名称',sortable:false,width:180,hidden:false,align:'center'}]	
	*	}
	*/
$.fn.easyGrid = function(_opts){
    		var _column = [];
    		for(var i=0;i<_opts.columns.length;i++){
    			var t = {};
    			t['data'] = _opts.columns[i]['field'];
    			t['orderable'] = typeof _opts.columns[i]['sortable'] == 'undefined' ? false : _opts.columns[i]['sortable'];
    			t['searchable'] = typeof _opts.columns[i]['searchable'] == 'undefined' ? false : _opts.columns[i]['searchable'];
    			t['sClass'] = typeof _opts.columns[i]['align'] == 'center' ? false : _opts.columns[i]['align'];
    			if(t['searchable']){
    				t['search']={'regex':true,value:''};
    			}
    			if(typeof _opts.columns[i]['createdCell'] == 'function' ){
    				t["createdCell"] = _opts.columns[i]['createdCell'];
    			}
    			if(typeof _opts.columns[i]['formatter'] == 'function' ){
    				t["render"] = _opts.columns[i]['formatter'];
    			}else{
    				t["render"] = function(data, type, rowData, meta){
    					var key = meta.settings.oAjaxData.columns[meta.col].data;
    					if(typeof key == 'string' && typeof rowData["__"+key] != 'undefined'){
    						return rowData["__"+key];
    					}
    					return data;
    				}
    			}
    			if(typeof _opts.columns[i]['hidden'] != 'undefined' && _opts.columns[i]['hidden']){
    				t['visible'] = false;
    			}
    			if(typeof _opts.columns[i]['title'] != 'undefined'){    				
    				t['title'] = _opts.columns[i]['title'];
    			}
    			_column.push(t);
    		};
    		_opts.columns = _column;
    		var params = $.extend({},_opts.data);
    		var _target = this;
    		var settings = $.extend({  
            	language:{
    	            "sProcessing": "处理中...",
    	            "sLengthMenu": "每页 _MENU_ 项",
    	            "sZeroRecords": "没有匹配结果",
    	            "sLengthMenu": "Display _MENU_ records", 
    	            "sInfo": "当前显示第 _START_ 至 _END_ 项；<span><a id='countQuery' href='javascript:void(0);' >[查看总计]</a></span>",//，共 _TOTAL_ 项
    	            "sInfoEmpty": "当前显示第 0 至 0 项，共 0 项",
    	            "sInfoFiltered": "",//"(由 _MAX_ 项结果过滤)",
    	            "sInfoPostFix": "",
    	            "sSearch": "搜索:",
    	            "sUrl": "",
    	            "sEmptyTable": "表中数据为空",
    	            "sLoadingRecords": "载入中...",
    	            "sInfoThousands": ",",   	            
    	            "oPaginate": {
    	                "sFirst": "首页",
    	                "sPrevious": "上页",
    	                "sNext": "下页",
    	                "sLast": "末页",
    	                "sJump": "跳转"
    	            },
    	            "oAria": {
    	                "sSortAscending": ": 以升序排列此列",
    	                "sSortDescending": ": 以降序排列此列"
    	            }
            	}, 
            	"iDisplayLength" : 10,  
            	"aLengthMenu" : [ [ 10,50, 100, 500 ], [ "10","50", "100", "500" ] ],
            	
                autoWidth: false,  //禁用自动调整列宽
                processing: true,  //隐藏加载提示,自行处理
                serverSide: true,  //启用服务器端分页
                searching: false,  //禁用原生搜索
                bDeferRender:false,
                bFilter:false,
                bScrollInfinite:false,
                "bJQueryUI":false,
                "bLengthChange":false,
                "sScrollX":"100%",
                "sScrollY":"100%",
                order: [],  //取消默认排序查询,否则复选框一列会出现小箭头
                renderer: "bootstrap",  //渲染样式：Bootstrap和jquery-ui
                pagingType: "simple_numbers",  //分页样式：simple,simple_numbers,full,full_numbers
                select: true,
    		    ajax: function (data, _callback, settings) {
    		    	if(typeof data['params'] == 'Object'){
    		    		params = $.extend(params,data['params']);
    		    	}
                    //封装请求参数
                    params.page_size = data.length;//页面显示记录条数，在页面显示每页显示多少项的时候
                    params.page = (data.start / data.length)+1;//当前页码
                    params.draw = data.draw;
                    if(typeof data.order != 'undefined' && data.order.length>0){
                    	var cidx = data.order[0]["column"];
                    	var dir = data.order[0]["dir"];
                    	var columnName= data.columns[cidx].data;
                    	params.order = columnName+" "+dir
                    }
                    data['params'] = params;
                    $.ajax({
                        type: "post",
                        url: getDomain()+'/svr/ajax/dataTables.json',
                        cache: false,  //禁用缓存
                        data: params,  //传入组装的参数
                        dataType: "json",
                        success : function (result) {
                        	if(typeof _callback == 'function'){
                        	  _callback(result);
                        	}
                        	//处理表格记录总数查询
                        	 $('#countQuery',_target.closest('.dataTables_wrapper')).data('currentParams',params).click(function(){
                        		var _queryArgs = $(this).data('currentParams');
                             	var textArea = $(this).parent();
                             	$.ajax({
                                    type: "post",
                                    url: getDomain()+'/svr/sys/ajax/queryCount.json',
                                    cache: false,  //禁用缓存
                                    data: _queryArgs,  //传入组装的参数
                                    dataType: "json",
                                    success : function (result) {
                                    	textArea.html('共计 '+result.data+' 条记录。');
                                    }
                                });
                             });
                        }
                    });
                },"fnInitComplete": function () {
                   
                },columns:[]},_opts);
    		
    		if(typeof _opts.buttons == 'object'){
    			for(var i=0;i<_opts.buttons.length;i++){
    				var conf = _opts.buttons[i];
    				if(conf['isNew']){//判定是新增数据操作
    					var header = $(_target).closest('.box').find('.box-header')
    					$('<button id="'+conf.act+'" class="btn btn-info btn-flat pull-right" style="margin-left:10px">'+conf.title+'</button>')
    					.appendTo(header)
    					.data('callback',conf.callback)
    					.click(function(){
    						$(this).data('callback').call(this);
    					});
    				}
    			}
    			
    			var operateColumn = {
                        "orderable": false,
                        "data": null,
                        "field":null,
                        'title':'操作',
                        'createdCell':function (td, cellData, rowData, row, col) {
                        	$(td).html('<div class="btn-group"></div>').data("createdCellData",rowData);//.data('setting.buttons',_opts.buttons);
                        	//var _htmls = '<div class="btn-group">';
                        	for(var i=0;i<_opts.buttons.length;i++){
                				var conf = _opts.buttons[i];
                				if(typeof conf['icon'] == 'undefined'){
	                				switch(conf.act){
	                				case 'insert':
	                					conf['icon'] = 'fa-plus';
	                					break;
	                				case 'update':
	                				case 'edit':
	                					conf['icon'] = 'fa-pencil-square-o';
	                					break;
	                				case 'delete':
	                					conf['icon'] = 'fa-trash-o';
	                					break;
	                				default:
	                						conf['icon'] = '';
	                				}
                				}
                				if(!conf['isNew']){//判定是新增数据操作
	                				//_htmls += '<button id="'+conf.act+'" class="btn btn-default" style="margin-left:5px" type="button">'+(typeof conf.title == 'string'?conf.title:conf.act)+'</button>';
	                				_htmls = '<a class="fa fa-fw '+conf['icon']+'" id="'+conf.act+'" title="'+(typeof conf.title == 'string'?conf.title:conf.act)+'" href="javascript:void(0);"></a>';
	                				$(_htmls).appendTo($('.btn-group',td)).data('callback',conf.callback).click(function(){
	                					var data = $(this).closest('td').data('createdCellData');
	                					if(typeof $(this).data('callback') == 'function' ){
	                						$(this).data('callback').call(this,data);
	                					}
	                				});
                				}
                			};
                        }
                    };   			
    			settings.columns.push(operateColumn);
    		}
    		var _grid = this.DataTable(settings);
    		
    		if(typeof _opts.search == 'object'){
    			$(this).gridSearch(_opts.search,_grid);
    		}
    		
    		return _grid;
 };
    	
 $.fn.gridSearch = function(_opts,_grid){
	 var searchContain = '<table  cellspacing="1" class="form-group"> <tr> ';
	 //var searchContain = '<div class="form-group row">';
	 for(var column in _opts){
		 var conf = $.extend({
			 title:'搜索',
			 equals:true,
			 type:'text',
			 width:'80px',
			 style:'',
			 'class':''
		 },_opts[column]);
		 conf.type = conf.type.toUpperCase();
		 _opts[column] = conf;
		switch(conf.type){
			case 'TEXT':
				searchContain += '<td style="padding-left:5px"><input class="form-control" type="text" id="'+column+'" title="'+conf.title+'" placeholder="'+conf.title+'" equals="'+conf.equals+'"   style="width:'+conf.width+';'+conf.style+'"/></td>';
				//searchContain += '<div class="col-sm-2"><input class="form-control" type="text" id="'+column+'" title="'+conf.title+'" value="'+conf.title+'" equals="'+conf.equals+'"   /></div>';
				break;
			case 'SELECT':
				searchContain += '<td style="padding-left:5px"><select class="form-control" id="'+column+'" placeholder="'+conf.title+'" equals="true"   style="width:'+conf.width+';'+conf.style+'"></select></td>';
				//searchContain += '<div class="col-sm-2"><select class="form-control" id="'+column+'" title="'+conf.title+'" equals="true"   ></select></div>';
				break;
			case 'DATE':
				searchContain += '<td style="padding-left:5px" title="双击清空日期选择"><button type="button" class="btn btn-default" id="'+column+'" text="'+conf.title+'"> <span> <i class="fa fa-calendar"></i> &nbsp;选择'+conf.title+'</span> <i class="fa fa-caret-down"></i> <input id="real_'+column+'" type="hidden"></button></td>';
				//searchContain += '<div class="col-sm-2" title="双击清空日期选择"><button type="button" class="btn btn-default" id="'+column+'" text="'+conf.title+'"> <span> <i class="fa fa-calendar"></i> &nbsp;选择'+conf.title+'</span> <i class="fa fa-caret-down"></i> <input id="real_'+column+'" type="hidden"></button></div>';
				break;
		}
	 }
	 
	 searchContain += ' <td style="padding-left:10px"><span class="glyphicon glyphicon-search" id="searchSubmit" style="cursor:pointer" title="点击搜索结果"></span> </td> </tr>  </table>';
	// searchContain += ' <div class="col-sm-1 pull-right"><span class="glyphicon glyphicon-search" id="searchSubmit" style="cursor:pointer" title="点击搜索结果"></span> </div></div>';
	 var target = $(this).closest('.dataTables_wrapper').before(searchContain).prev();
	 /*
	  * .click(function(){
		 if($(this).attr('type') == 'text' && $(this).val() == $(this).attr('title')){
				$(this).val('');
		 }
		 //console.log($(this).val());
	 }).mouseout(function(){					
			if($(this).val() == ''){
				$(this).val($(this).attr('title'));
			}
	})
	  * */
	 target.find(':input').each(function(){//初始化各类型数据填充
		var key = $(this).attr('id');
		//alert(key);
		if(typeof _grid.context[0].oAjaxData.params[key] != 'undefined'){
			$(this).val(_grid.context[0].oAjaxData.params[key]);
		}
		var conf = _opts[key];
		if(typeof conf == 'object'){	
		  switch(conf.type){
			case 'SELECT':
				conf.first = conf.title+'-全部';
				$(this).selection(conf);
				//console.log(conf);
				break;
			case 'DATE':
//				console.log($(this).html());
				var id = $(this).attr('id');
				$(this).daterangepicker(
				        {
				          ranges: {
				            '今天': [moment(), moment()],
				            '昨天': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
				            '最近7天': [moment().subtract(6, 'days'), moment()],
				            '最近30天': [moment().subtract(29, 'days'), moment()],
				            '最近三个月': [moment().subtract(2, 'month').startOf('month'), moment().endOf('month')],
				            '最近六个月': [moment().subtract(5, 'month').startOf('month'), moment().endOf('month')]
				           // '当前月份': [moment().startOf('month'), moment().endOf('month')],
				           // '上个月份': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
				          },
				         // startDate: moment().subtract(29, 'days'),
				         // endDate: moment(),
				          format: 'YYYY-MM-DD',
				          locale:{  
	                          applyLabel : '确定',  
	                          cancelLabel : '取消',  
	                          fromLabel : '起始时间',  
	                          toLabel : '结束时间',  
	                          customRangeLabel : '自定义日期',  
	                          daysOfWeek : [ '日', '一', '二', '三', '四', '五', '六' ],  
	                          monthNames : [ '一月', '二月', '三月', '四月', '五月', '六月','七月', '八月', '九月', '十月', '十一月', '十二月' ]  
	                      } 
				        },
				        function (start, end) {
				          $('span','#'+id).html(start.format('YYYY-MM-DD') + ' 至 ' + end.format('YYYY-MM-DD'));
				          $('#real_'+id,'#'+id).val(">="+start.format('YYYY-MM-DD')+" 00:00:00&<="+end.format('YYYY-MM-DD')+' 23:59:59');
				        }
				    );
				$(this).dblclick(function(){
				//	console.log($(this).html());
					var id = $(this).attr('id');
					$('span',this).html('<i class="fa fa-calendar"></i> &nbsp;选择'+$(this).attr('text'));
			        $('#real_'+id,this).val('');
				});
				break;
			default :				
		   }
		}
	});
	
	 
	target.find('#searchSubmit').click(function(){
		var setting = $(this).data('setting');	
		gridParams = _grid.context[0].oAjaxData.params;
		var data = $(this).parent().parent().getData();
		for(var key in data){
			if(key.indexOf("real_") == 0){//兼容作为特殊处理方式的数据存储
				data[key.substr(5)]  = data[key];
				delete data[key];
			}
		}
		for(var key in setting){
			var conf = setting[key];
			var val = data[key];
			if(typeof val == 'string' && val != conf.title){
				if(conf['equals']||val.indexOf('*')>-1){
					_grid.context[0].oAjaxData.params[key] = val;
				}else{
					_grid.context[0].oAjaxData.params[key] = '%'+val+'%';
				}
			}else{
				delete _grid.context[0].oAjaxData.params[key];
			}
		}
		//console.log(_grid.context[0].oAjaxData.params);
		_grid.ajax.reload();
	}).data('setting',_opts);
	
	target.setData(_grid.context[0].oAjaxData.params);
 };
 
 /**
  * 获取对象内的表单内容
  */
 $.fn.getData = function(fullData,isSensitiveCheck,isCheckScript){
		if(typeof isCheckScript == 'undefined')isCheckScript=true;
		var tmpJson = {};
		var getMultiSelectValue = function(key,_select){
			_select.find('option').each(function(){
				if(this.selected){
					if(tmpJson[key]==null)
						tmpJson[key] = $(this).val();
					else
						tmpJson[key] += ';'+$(this).val();
				}
			});
		};
		this.find(':input').each(function(){
			_thisType = $(this).attr('type');
			var dataKey = $(this).attr('name')||$(this).attr('id');
			if(dataKey==null || dataKey =='' || $(this).is('.combo-value'))return;
			if($(this).is('.combo-f')){
				if($(this).combo('options').multiple)
					tmpJson[dataKey] = $(this).combo('getValues').join(',');
				else
					tmpJson[dataKey] = $(this).combo('getValue');
			}else if($(this).is('.combobox-f')){
				if($(this).combobox('options').multiple){//XXX
					tmpJson[dataKey] = $(this).combobox('getValues').join(',');
				}else{
					tmpJson[dataKey] = $(this).combobox('getValue');
				}
			}else if($(this).is('.combotree-f')){
				if($(this).combotree('options').multiple){//XXX
					tmpJson[dataKey] = $(this).combotree('getValues').join(',');
				}else{
					tmpJson[dataKey] = $(this).combotree('getValue');
				}
				//tmpJson[dataKey] = $(this).combotree('getValue');
			}else if($(this).is('.datebox-f')){						
				tmpJson[dataKey] = $(this).datebox('getValue');
			}else if($(this).is('.datetimebox-f')){
				tmpJson[dataKey] = $(this).datetimebox('getValue');
			}else if($(this).is('.easyui-numberbox')){
				tmpJson[dataKey] = $(this).numberbox('getValue');
			}else{
				if(_thisType == 'radio' || _thisType == 'checkbox'){
					if(this.checked){
						if(tmpJson[dataKey]==null){
							tmpJson[dataKey] = $(this).val();
						}else{
							tmpJson[dataKey] += ';'+$(this).val();
						}
					}
				}else if(_thisType=="select-multiple"){
					getMultiSelectValue(dataKey,$(this));
				}else if(_thisType=="select-one"){//XXX  baoft 
					var tVal = $(this).val();
					if(tVal!=null&&tVal.length>0){
						tmpJson[dataKey] = tVal;
					}else{						
						tmpJson[dataKey] = tVal;						
					};

				}else{
					// type attribute of input tag [button;checkbox;file;hidden;password;radio;reset;submit;text]
					if('button;reset;submit'.indexOf(_thisType)==-1){
						if(fullData){
							var tVal = $(this).val();
							if(tmpJson[dataKey]==null)
								tmpJson[dataKey] = tVal;
							else tmpJson[dataKey] = tmpJson[dataKey]+';'+tVal;
						}else{
							var tVal = $(this).val();
							if(tVal!=null&&tVal.length>0){
								if(tmpJson[dataKey]==null)
									tmpJson[dataKey] = tVal;
								else tmpJson[dataKey] = tmpJson[dataKey]+';'+tVal;
							}				
						}
					}
				}
			};
			if(!fullData && typeof tmpJson[dataKey] == 'string' && tmpJson[dataKey].replace(/^ | $/g,'').length ==0)delete tmpJson[dataKey];
		});

		//if(isCheckScript)tmpJson = checkScript(tmpJson);//XXX 暂时不需要处理过滤JS文本
		return tmpJson;
	};
	
	$.fn.setData = function(_data,_formatParam){
		var formatParam = $.extend({
			clear:true,//情况表单所有内容
			isQDevelopData:false,//表单所有数据KEY均为大写
			isFormOnly:true,//仅仅将数据展示在表单内
			readOnly:false,//将表单设置成只查看状态
			filter:[],//过滤不操作的表单内容
			format:null//数据值设置自定义
		},_formatParam);
		//if(formatParam.clear)$(this).form('clear');//采用easy-ui的form clear 方法清除数据
		var isIe6 = isIE6();
		var setMultiSelectValue = function(_select,value){
			_select.find('option').each(function(){
				if(value.indexOf(';'+$(this).val()+';')>-1){
					if(isIe6){
						try{
							this.selected = true;
						}catch(e){}					
					}else{
						$(this).attr('selected','selected') ;
					}
				}
			});
			_select.change();
		};
		var data = {};
		if(formatParam.isQDevelopData){
			for(each in _data)data[each.toUpperCase()] = _data[each];
		}else{
			data = $.extend({},_data);
		};
		if(data == null)return;
		var multiKey = [];
		this.find(':input').each(function(){
			var _dValue = '';
			_thisId = $(this).attr('id');
			_thisName = $(this).attr('name');
			_thisType = $(this).attr('type');
			if((!_thisId&&!_thisName)||_thisType=='button')return;
			key = _thisId||_thisName;
			if(formatParam.isQDevelopData)key = key.toUpperCase();
			_dValue = data[key];
			if(_dValue == null||formatParam.filter.contains(key))return;

			if(formatParam.format!=null&&
					(typeof formatParam.format[_thisType] == 'function'||typeof formatParam.format[_thisId] == 'function')){
				if(typeof formatParam.format[_thisType] == 'function'){
					formatParam.format[_thisType](this,_dValue);
				}else{formatParam.format[_thisId](this,_dValue);};
			}else{
				if(_dValue!=null){
					if($(this).is('.combo-f')){
						if($(this).combo('options').multiple)
							$(this).combo('setValues',typeof _dValue == 'object'?_dValue:_dValue.split(","));
						else
							$(this).combo('setValue',_dValue);
					}if($(this).is('.combobox-f')){
						if($(this).combobox('options').multiple)
							$(this).combobox('setValues',typeof _dValue == 'object'?_dValue:_dValue.split(","));
						else
							$(this).combobox('setValue',_dValue);
					}else if($(this).is('.combotree-f')){			
						if($(this).combotree('options').multiple)
							$(this).combotree('setValues',typeof _dValue == 'object'?_dValue:_dValue.split(","));
						else
							$(this).combotree('setValue',_dValue);
					}else if($(this).is('.datebox-f')){						
						$(this).datebox('setValue',_dValue);
					}else if($(this).is('.datetimebox-f')){
						$(this).datetimebox('setValue',_dValue);
					}else if($(this).is('.easyui-numberbox')){
						$(this).numberbox('setValue',_dValue);
					}else{						
						if(_thisType == 'radio' || _thisType == 'checkbox'){
							var checkValue = ';'+_dValue+';';
							if(_dValue!=null&&checkValue.indexOf(';'+this.value+';') > -1)
								$(this).attr('checked','checked');	
						}else if(_thisType=="select-one"||_thisType=="select-multiple"){
							var checkValue = ';'+_dValue+';';
							if(_dValue!=null)setMultiSelectValue($(this),checkValue);				
						}else {
							if(typeof _dValue =='number' ||( typeof _dValue =='string' && _dValue.toUpperCase()!='NULL'))$(this).val(_dValue);
						}
					}
				}else{
					$(this).val('');
				}
			};
			if(!formatParam.isFormOnly)multiKey.push(key);			
		});

		if(!formatParam.isFormOnly){
			for(var i=0;i<multiKey.length;i++){
				delete data[multiKey[i]];
			};
			var _targetForm = this;
			for(each in data){
				var target = $('#'+(formatParam.isQDevelopData?each.toLowerCase():each),_targetForm);
				if(typeof target == 'object'){
					target.html(data[each]);
				}
			}
		};

		if(formatParam.readOnly){
			this.find(':input').each(function(){
				_thisId = $(this).attr('id');
				_thisName = $(this).attr('name');
				_thisType = $(this).attr('type');
				if(!_thisId&&!_thisName)return;
				if(formatParam.format!=null&&(typeof formatParam.format[_thisType] == 'function'
					||typeof formatParam.format[_thisId] == 'function')){
					if(typeof formatParam.format[_thisType] == 'function'){
						formatParam.format[_thisType](this);
					}else{
						formatParam.format[_thisId](this);
					}
				}else{
					$(this).attr('disabled','true');
					/*
					if(_thisType == 'radio' || _thisType == 'checkbox'){
					}else if(_thisType=="select-one"||_thisType=="select-multiple"){
						$(this).after($(this).find(':selected').text()).remove();
					}else{
						if(_thisType != 'hidden' && _thisType != 'button')$(this).after($(this).val()).remove();;
					}
					 */
				}
			});
			
		}else{
			this.find(':input').each(function(){
				$(this).removeAttr('disabled');
			});
		}
		return this;
	};
	
	/** 
	@constructor
	@description	动态Select元素值设定<br>
		支持静态值，ajax数据库查询值等数据源<br>
		支持SELECT联动
	@param {Object} opt 具体参数设定

	@example 
		基本值设定Demo：
		====静态数组====
		例1：select的text和value一致时可用
		$('#selectId').selection({
			options:["1","2","3"]
		})

		例2：{key:value}结构数据可用
		$('#selectId').selection({
			options:{value1:text1,value2:text2,value3:text3...}
		})

		例3：对象数组可用
		$('#selectId').selection({
			options:[{text:'显示1',value:'值1'},{text:'显示2',value:'值2'}...]
		})

		例4:对象数组自定义显示结构
		$('#selectId').selection({
			text:'name',value:'id',
			options:[{id:'显示1',name:'值1'},{id:'显示2',name:'值2'}...]
		})

		例5:对象数组自定义方法
		$('#selectId').selection({
			text:function(data){//data为数组中的每个对象数据
				return '=='+data['name']+'==';
			},value:function(data){
				return data['id']+'%';
			},
			options:[{id:'显示1',name:'值1'},{id:'显示2',name:'值2'}...]
		})

		例6：ajax数据库获取options值
		$('#selectId').selection({
			text:'ID',name:'VALUE',//该处值为大写，ajax方式获取数组中的对象的KEY均为大写
			ajax:{
				index:'sqlConfig',//获取
				//other sql param
			}
		})

		例7：联动操作(支持N级联动操作)
		$('#selectId').selection({
			text:'ID',name:'VALUE',//该处值为大写，ajax方式获取数组中的对象的KEY均为大写
			ajax:{
				index:'sqlConfig',//获取
				//other sql param
			},
			filterParent:'#fatherDomId',//父选择框的ID 最多递归往上找5层找其父选择框
			filterParentWith:'pid'//父选择框选定值后，筛选该数组对象中key为pid的值和父类相等的值
		})

	 */
	$.fn.selection = function(opt,param){
		if(typeof opt == 'string'){
			$.fn.selection.methods[opt](this,param);
			return;
		};
		opt = $.extend(true,{text:'text',value:'value',__options:{textLength:15}},opt);
		if(opt==null)return;
		var _select = this;
		var _filterParentValue;
		var isFilterParent = typeof opt.filterParent!='undefined' && typeof opt.filterParentWith!='undefined' ? true:false;
		var _defaultKey = "default";
		// 初始话Select值
		initSelect = function(){
			if(opt.first){
				if(typeof opt.first == 'string'){
					_select.html('<option  value="">'+opt.first+'</option>');
				}else if(typeof opt.first == 'object'){	
					_select.html('<option value="'+(opt.first["value"])+'">'+(opt.first["text"])+'</option>');
				}else{
					_select.html('<option value="">请选择...</option>');
				}
			}else _select.html('');
		};
		__parseValue = function(key,data){
			if(typeof data == 'string') return data;
			else if(typeof key == 'string')return data[key];
			else if(typeof key == 'function')return key(data);
			else return "";
		};

		__checkFilterData = function(data){
			if(typeof opt.filter == 'undefined')return true;
			return opt.filter(data);
		};

		__formatOptions = function(_option){ 
			if(typeof _option.length == 'undefined'){//对象数据
				opt.__options[_defaultKey] = [];
				for(var value in _option){
					var text = _option[value];
					_data = {'value':value,'text':text};
					if(__checkFilterData(_data))
						opt.__options[_defaultKey] .push(_data);
				} 
			}else{//数组数据
				if(isFilterParent){
					$.each(_option,function(i,data){
						if(__checkFilterData(data)&&data[opt.filterParentWith]!=null){
							if(opt.__options[data[opt.filterParentWith]]==null)opt.__options[data[opt.filterParentWith]]=[];
							opt.__options[data[opt.filterParentWith]].push({'value':__parseValue(opt.value,data),'text':__parseValue(opt.text,data)});
						}
					});
				}else{
					opt.__options[_defaultKey] = [];	   
					$.each(_option,function(i,data){
						if(__checkFilterData(data))
							opt.__options[_defaultKey].push({'value':__parseValue(opt.value,data),'text':__parseValue(opt.text,data)});
					});
				}
			};
			delete opt.options;
			if(typeof opt.appendItem!='undefined'){

			}
		};

		__parseOption = function(indexValue){
			initSelect();
			indexValue = indexValue == null ? _defaultKey : indexValue;
			if(opt.__options[indexValue] == null)return;
			var _selectDom = _select[0];
			$.each(opt.__options[indexValue],function(i,data){
				if(data.text == null) data.text = data.value;
				if(opt.__options['textLength'] < data.text.length){
					_selectDom.options.add(new Option(data.text.substring(0,opt.__options['textLength'])+"...",data.value));
				}else{
					//console.log(data.text+' - '+data.value);
					_selectDom.options.add(new Option(data.text,data.value));
				}
			});
			_select.change(); 
		};

		__regParentFilter = function(){
			if(isFilterParent){
				var _filterParent=null;
				if(typeof opt.filterParent == 'object')
					_filterParent =  opt.filterParent;
				else if(typeof opt.filterParent == 'string'){
					_filterParent = $(_select).siblings(opt.filterParent);
					if(_filterParent.length==0){
						var deep = 0,_e = $(_select).parent();
						while(_filterParent.length==0&&deep<5){			
							_filterParent = _e.find(opt.filterParent);	
							_e = _e.parent();	
							deep++;			
						}
					}
				};
				_filterParent.change(function(){
					if(opt.first){
						if(typeof opt.first == 'string'){
							_select.html('<option  value="">'+opt.first+'</option>');
						}else if(typeof opt.first == 'object'){	
							_select.html('<option value="'+(opt.first["value"])+'">'+(opt.first["text"])+'</option>');
						}else{
							_select.html('<option value="">请选择...</option>');
						}
					}else _select.html('');
					var pVale = _filterParent.val();		
					if(pVale != null && pVale != ''&&opt.__options[pVale]!=null){
						var _selectDom = _select[0];
						if(typeof opt.beforeOptions =='function')opt.beforeOptions(_selectDom);
						$.each(opt.__options[pVale],function(i,data){
							if(opt.__options['textLength'] < data.text.length){
								_selectDom.options.add(new Option(data.text.substring(0,opt.__options['textLength'])+"...",data.value));
							}else{
								_selectDom.options.add(new Option(data.text,data.value));
							}
						});
						if(typeof opt.afterOptions =='function')opt.afterOptions(_selectDom);
					};
					_select.change(); 	
				});		
			}
		};

		if(typeof opt.options != 'undefined'){
			__formatOptions(opt.options);
			__parseOption();
			__regParentFilter();	
		}else if(typeof opt.ajax == 'object' ){
			var ajaxArgs = {async:false};
			if(typeof opt.url == 'string'){
				ajaxArgs[url] = opt.url;
			}
			ajaxArgs['data'] = opt.ajax;
			ajaxQuery(ajaxArgs,function(rb){
				__formatOptions(rb);
				__parseOption();
				__regParentFilter();	
			});
		}

	};
	$.fn.selection.methods = {
			selected:function(_this,param){
				_select = _this.get(0);
				for(var i=0;i<_select.options.length;i++){				
					if(_select.options[i].value == param){
						_select.options[i].selected = true;
					}else  _select.options[i].selected = false;
				}
			}
	};
});


/** 
@constructor
@description ajax方式查询数据库
*/
function ajaxQuery(__opts,callback){
	var args = $.extend({
		scriptCharset:'UTF-8',
		ifModified:true,
		cache:false,
		url:getDomain()+'/svr/ajax/dataTables.json',
		async:true,
		timeOut:60000,
		type:"POST",
		dataType:'json',
		success:function(r){
			if(r.errMsg.length == 0){
				callback(r.data);
			}else{
				alert(r.errMsg)
//				if(confirm('您当前没有权限请求该接口，是否确定重新登陆？')){
//					window.location.href = getDomain()+"/sys/login/login.html"
//				}
			}
		},
		error:function(r,t,e){
			
		}
	},__opts);
	$.ajax(args);
};

/** 
@constructor
@description ajax方式更新数据库
*/
function ajaxUpdate(__opts,callback){
	var args = $.extend({
		scriptCharset:'UTF-8',
		ifModified:true,
		cache:false,
		url:getDomain()+'/svr/sys/ajax/formCommit.json',
		async:true,
		timeOut:60000,
		type:"POST",
		dataType:'json',
		success:function(r){
			if(r.success){
				callback(r.data,r);
			}else{
				alert(r.error);
			}
		},
		error:function(r,t,e){
			
		}
	},__opts);
	$.ajax(args);
};

/**
 * 默认系统接受更新方式，提交参数
 * @param formData 请求数据
 * @param form	表单对象，用来关闭表单
 * @param grid	表格对象，用来更新表格数据
 */
function ajaxSubmit(formData,form,grid){
	$.ajax({
		scriptCharset:'UTF-8',
		ifModified:true,
		cache:false,
		url:getDomain()+'/svr/sys/ajax/formCommit.json',
		data:formData,
		async:true,
		timeOut:6000,
		type:"POST",
		dataType:'json',
		success:function(r){
			if(r.tag != 0){
				alert(r.errMsg);
			}else{
				if(typeof form == 'object'){
					$(form).modal('hide');
				}
				if(typeof grid == 'object'){
					grid.ajax.reload();
				}
			}
		},
		error:function(r,t,e){
		}
	});
}

/***
 * 根据自定义的URL，提交数据
 * @param url	自定义URL
 * @param formData 请求数据
 * @param form	表单对象，用来关闭表单
 * @param grid	表格对象，用来更新表格数据
 */
function ajaxSubmitUrl(url,formData,form,grid){
	if(typeof url != 'string'){
		alert('需要传入自定义URL');
		return;
	}
	$.ajax({
		scriptCharset:'UTF-8',
		ifModified:true,
		cache:false,
		url:getDomain()+url,
		data:formData,
		async:true,
		timeOut:6000,
		type:"POST",
		dataType:'json',
		success:function(r){
			if(r.tag != 0){
				alert(r.errMsg);
			}else{
				if(typeof form == 'object'){
					$(form).modal('hide');
				}
				if(typeof grid == 'object'){
					grid.ajax.reload();
				}
			}
		},
		error:function(r,t,e){
		}
	});
}

function isIE6() {
	try{
		if ($.browser.msie) {if ($.browser.version == "6.0") return true;}
	}catch(e){
	}
	return false;
};
function getDomain(){return typeof domain == 'string'?domain:'';};

Date.prototype.format = function(format){
	  var o = {
	    "M+" : this.getMonth()+1, //month
	    "d+" : this.getDate(),    //day
	    "h+" : this.getHours(),   //hour
	    "m+" : this.getMinutes(), //minute
	    "s+" : this.getSeconds(), //second
	    "q+" : Math.floor((this.getMonth()+3)/3),  //quarter
	    "S" : this.getMilliseconds() //millisecond
	  };
	  if(/(y+)/.test(format)) format=format.replace(RegExp.$1,
	    (this.getFullYear()+"").substr(4 - RegExp.$1.length));
	  for(var k in o)if(new RegExp("("+ k +")").test(format))
	    format = format.replace(RegExp.$1,
	      RegExp.$1.length==1 ? o[k] :
	        ("00"+ o[k]).substr((""+ o[k]).length));
	  return format;
	};

	Array.prototype.contains = function(value){
		if(typeof value == 'object'){
			for(var i=0;i<this.length;i++){
				for(var j=0;j<value.length;j++){
					if(this[i] == value[j])return true;
				}
			}
		}else{
			for(var i=0;i<this.length;i++){
				if(this[i] == value)return true;
			}
		}
		return false;
};

function debug(o){
	console.log(toString(o));
};

var toString = function(o){
	var r = [];
	if(typeof o =="string") return "\""+o.replace(/([\'\"\\])/g,"\\$1").replace(/(\n)/g,"\\n").replace(/(\r)/g,"\\r").replace(/(\t)/g,"\\t")+"\"";
	if(typeof o =="undefined") return "undefined";
	if(typeof o == "object"){
		if(o===null) return "null";
		else if(!o.sort){
			for(var i in o)
				r.push(i+":"+toString(o[i]));
			r="{"+r.join()+"}";
		}else{
			for(var i =0;i<o.length;i++)
				r.push(toString(o[i]));
			r="["+r.join()+"]";
		};
		return r;
	};
	return o.toString();
};
String.prototype.MD5 = function (bit)
{
    var sMessage = this;
    function RotateLeft(lValue, iShiftBits) { return (lValue<<iShiftBits) | (lValue>>>(32-iShiftBits)); } 
    function AddUnsigned(lX,lY)
    {
        var lX4,lY4,lX8,lY8,lResult;
        lX8 = (lX & 0x80000000);
        lY8 = (lY & 0x80000000);
        lX4 = (lX & 0x40000000);
        lY4 = (lY & 0x40000000);
        lResult = (lX & 0x3FFFFFFF)+(lY & 0x3FFFFFFF); 
        if (lX4 & lY4) return (lResult ^ 0x80000000 ^ lX8 ^ lY8); 
        if (lX4 | lY4)
        { 
            if (lResult & 0x40000000) return (lResult ^ 0xC0000000 ^ lX8 ^ lY8); 
            else return (lResult ^ 0x40000000 ^ lX8 ^ lY8); 
        } else return (lResult ^ lX8 ^ lY8); 
    } 
    function F(x,y,z) { return (x & y) | ((~x) & z); } 
    function G(x,y,z) { return (x & z) | (y & (~z)); } 
    function H(x,y,z) { return (x ^ y ^ z); } 
    function I(x,y,z) { return (y ^ (x | (~z))); } 
    function FF(a,b,c,d,x,s,ac)
    { 
        a = AddUnsigned(a, AddUnsigned(AddUnsigned(F(b, c, d), x), ac)); 
        return AddUnsigned(RotateLeft(a, s), b); 
    } 
    function GG(a,b,c,d,x,s,ac)
    { 
        a = AddUnsigned(a, AddUnsigned(AddUnsigned(G(b, c, d), x), ac)); 
        return AddUnsigned(RotateLeft(a, s), b); 
    } 
    function HH(a,b,c,d,x,s,ac)
    { 
        a = AddUnsigned(a, AddUnsigned(AddUnsigned(H(b, c, d), x), ac)); 
        return AddUnsigned(RotateLeft(a, s), b); 
    } 
    function II(a,b,c,d,x,s,ac)
    { 
        a = AddUnsigned(a, AddUnsigned(AddUnsigned(I(b, c, d), x), ac)); 
        return AddUnsigned(RotateLeft(a, s), b); 
    } 
    function ConvertToWordArray(sMessage)
    { 
        var lWordCount; 
        var lMessageLength = sMessage.length; 
        var lNumberOfWords_temp1=lMessageLength + 8; 
        var lNumberOfWords_temp2=(lNumberOfWords_temp1-(lNumberOfWords_temp1 % 64))/64; 
        var lNumberOfWords = (lNumberOfWords_temp2+1)*16; 
        var lWordArray=Array(lNumberOfWords-1); 
        var lBytePosition = 0; 
        var lByteCount = 0; 
        while ( lByteCount < lMessageLength )
        { 
            lWordCount = (lByteCount-(lByteCount % 4))/4; 
            lBytePosition = (lByteCount % 4)*8; 
            lWordArray[lWordCount] = (lWordArray[lWordCount] | (sMessage.charCodeAt(lByteCount)<<lBytePosition)); 
            lByteCount++; 
        } 
        lWordCount = (lByteCount-(lByteCount % 4))/4; 
        lBytePosition = (lByteCount % 4)*8; 
        lWordArray[lWordCount] = lWordArray[lWordCount] | (0x80<<lBytePosition); 
        lWordArray[lNumberOfWords-2] = lMessageLength<<3; 
        lWordArray[lNumberOfWords-1] = lMessageLength>>>29; 
        return lWordArray; 
    } 
    function WordToHex(lValue)
    { 
        var WordToHexValue="",WordToHexValue_temp="",lByte,lCount; 
        for (lCount = 0;lCount<=3;lCount++)
        { 
            lByte = (lValue>>>(lCount*8)) & 255; 
            WordToHexValue_temp = "0" + lByte.toString(16); 
            WordToHexValue = WordToHexValue + WordToHexValue_temp.substr(WordToHexValue_temp.length-2,2); 
        } 
        return WordToHexValue; 
    } 
    var x=Array(); 
    var k,AA,BB,CC,DD,a,b,c,d 
    var S11=7, S12=12, S13=17, S14=22; 
    var S21=5, S22=9 , S23=14, S24=20; 
    var S31=4, S32=11, S33=16, S34=23; 
    var S41=6, S42=10, S43=15, S44=21; 
    // Steps 1 and 2. Append padding bits and length and convert to words 
    x = ConvertToWordArray(sMessage); 
    // Step 3. Initialise 
    a = 0x67452301; b = 0xEFCDAB89; c = 0x98BADCFE; d = 0x10325476; 
    // Step 4. Process the message in 16-word blocks 
    for (k=0;k<x.length;k+=16)
    { 
        AA=a; BB=b; CC=c; DD=d; 
        a=FF(a,b,c,d,x[k+0], S11,0xD76AA478); 
        d=FF(d,a,b,c,x[k+1], S12,0xE8C7B756); 
        c=FF(c,d,a,b,x[k+2], S13,0x242070DB); 
        b=FF(b,c,d,a,x[k+3], S14,0xC1BDCEEE); 
        a=FF(a,b,c,d,x[k+4], S11,0xF57C0FAF); 
        d=FF(d,a,b,c,x[k+5], S12,0x4787C62A); 
        c=FF(c,d,a,b,x[k+6], S13,0xA8304613); 
        b=FF(b,c,d,a,x[k+7], S14,0xFD469501); 
        a=FF(a,b,c,d,x[k+8], S11,0x698098D8); 
        d=FF(d,a,b,c,x[k+9], S12,0x8B44F7AF); 
        c=FF(c,d,a,b,x[k+10],S13,0xFFFF5BB1); 
        b=FF(b,c,d,a,x[k+11],S14,0x895CD7BE); 
        a=FF(a,b,c,d,x[k+12],S11,0x6B901122); 
        d=FF(d,a,b,c,x[k+13],S12,0xFD987193); 
        c=FF(c,d,a,b,x[k+14],S13,0xA679438E); 
        b=FF(b,c,d,a,x[k+15],S14,0x49B40821); 
        a=GG(a,b,c,d,x[k+1], S21,0xF61E2562); 
        d=GG(d,a,b,c,x[k+6], S22,0xC040B340); 
        c=GG(c,d,a,b,x[k+11],S23,0x265E5A51); 
        b=GG(b,c,d,a,x[k+0], S24,0xE9B6C7AA); 
        a=GG(a,b,c,d,x[k+5], S21,0xD62F105D); 
        d=GG(d,a,b,c,x[k+10],S22,0x2441453); 
        c=GG(c,d,a,b,x[k+15],S23,0xD8A1E681); 
        b=GG(b,c,d,a,x[k+4], S24,0xE7D3FBC8); 
        a=GG(a,b,c,d,x[k+9], S21,0x21E1CDE6); 
        d=GG(d,a,b,c,x[k+14],S22,0xC33707D6); 
        c=GG(c,d,a,b,x[k+3], S23,0xF4D50D87); 
        b=GG(b,c,d,a,x[k+8], S24,0x455A14ED); 
        a=GG(a,b,c,d,x[k+13],S21,0xA9E3E905); 
        d=GG(d,a,b,c,x[k+2], S22,0xFCEFA3F8); 
        c=GG(c,d,a,b,x[k+7], S23,0x676F02D9); 
        b=GG(b,c,d,a,x[k+12],S24,0x8D2A4C8A); 
        a=HH(a,b,c,d,x[k+5], S31,0xFFFA3942); 
        d=HH(d,a,b,c,x[k+8], S32,0x8771F681); 
        c=HH(c,d,a,b,x[k+11],S33,0x6D9D6122); 
        b=HH(b,c,d,a,x[k+14],S34,0xFDE5380C); 
        a=HH(a,b,c,d,x[k+1], S31,0xA4BEEA44); 
        d=HH(d,a,b,c,x[k+4], S32,0x4BDECFA9); 
        c=HH(c,d,a,b,x[k+7], S33,0xF6BB4B60); 
        b=HH(b,c,d,a,x[k+10],S34,0xBEBFBC70); 
        a=HH(a,b,c,d,x[k+13],S31,0x289B7EC6); 
        d=HH(d,a,b,c,x[k+0], S32,0xEAA127FA); 
        c=HH(c,d,a,b,x[k+3], S33,0xD4EF3085); 
        b=HH(b,c,d,a,x[k+6], S34,0x4881D05); 
        a=HH(a,b,c,d,x[k+9], S31,0xD9D4D039); 
        d=HH(d,a,b,c,x[k+12],S32,0xE6DB99E5); 
        c=HH(c,d,a,b,x[k+15],S33,0x1FA27CF8); 
        b=HH(b,c,d,a,x[k+2], S34,0xC4AC5665); 
        a=II(a,b,c,d,x[k+0], S41,0xF4292244); 
        d=II(d,a,b,c,x[k+7], S42,0x432AFF97); 
        c=II(c,d,a,b,x[k+14],S43,0xAB9423A7); 
        b=II(b,c,d,a,x[k+5], S44,0xFC93A039); 
        a=II(a,b,c,d,x[k+12],S41,0x655B59C3); 
        d=II(d,a,b,c,x[k+3], S42,0x8F0CCC92); 
        c=II(c,d,a,b,x[k+10],S43,0xFFEFF47D); 
        b=II(b,c,d,a,x[k+1], S44,0x85845DD1); 
        a=II(a,b,c,d,x[k+8], S41,0x6FA87E4F); 
        d=II(d,a,b,c,x[k+15],S42,0xFE2CE6E0); 
        c=II(c,d,a,b,x[k+6], S43,0xA3014314); 
        b=II(b,c,d,a,x[k+13],S44,0x4E0811A1); 
        a=II(a,b,c,d,x[k+4], S41,0xF7537E82); 
        d=II(d,a,b,c,x[k+11],S42,0xBD3AF235); 
        c=II(c,d,a,b,x[k+2], S43,0x2AD7D2BB); 
        b=II(b,c,d,a,x[k+9], S44,0xEB86D391); 
        a=AddUnsigned(a,AA); b=AddUnsigned(b,BB); c=AddUnsigned(c,CC); d=AddUnsigned(d,DD); 
    }
    if(bit==16){
        return WordToHex(b)+WordToHex(c);
    }else{
        return WordToHex(a)+WordToHex(b)+WordToHex(c)+WordToHex(d);
    }
}