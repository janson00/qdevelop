  <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%--   <%@ page import="java.util.*" %>
 --%>
 <header class="main-header">
    <!-- Logo -->
    <a href="#" class="logo">
      <!-- mini logo for sidebar mini 50x50 pixels -->
      <span class="logo-mini"><b>管</b></span>
      <!-- logo for regular state and mobile devices -->
      <span class="logo-lg"><b>管理后台</b></span>
    </a>
    <!-- Header Navbar: style can be found in header.less -->
    <nav class="navbar navbar-static-top">
      <!-- Sidebar toggle button-->
      <a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button">
        <span class="sr-only">Toggle navigation</span>
      </a>

      <div class="navbar-custom-menu">
        <ul class="nav navbar-nav">
          <!-- Messages: style can be found in dropdown.less-->
         
          <!-- Notifications: style can be found in dropdown.less -->
          <li class="dropdown notifications-menu">
            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
              <i class="fa fa-bell-o"></i>
              <span class="label label-warning">0</span>
            </a>
            <ul class="dropdown-menu">
              <!-- <li class="header">You have 10 notifications</li>
              <li>
                inner menu: contains the actual data
                <ul class="menu">
                  <li>
                    <a href="#">
                      <i class="fa fa-users text-aqua"></i> 5 new members joined today
                    </a>
                  </li>
                  <li>
                    <a href="#">
                      <i class="fa fa-warning text-yellow"></i> Very long description here that may not fit into the
                      page and may cause design problems
                    </a>
                  </li>
                  <li>
                    <a href="#">
                      <i class="fa fa-users text-red"></i> 5 new members joined
                    </a>
                  </li>
                  <li>
                    <a href="#">
                      <i class="fa fa-shopping-cart text-green"></i> 25 sales made
                    </a>
                  </li>
                  <li>
                    <a href="#">
                      <i class="fa fa-user text-red"></i> You changed your username
                    </a>
                  </li>
                </ul>
              </li>
              <li class="footer"><a href="#">View all</a></li> -->
              
            </ul>
            
          </li>
         
          <!-- User Account: style can be found in dropdown.less -->
          <li class="dropdown user user-menu">
            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
              <img src="http://wx.qlogo.cn/mmopen/F3xiaBEkH1VJ1Bv7yBx9dMbMB14LtknWsBEl4pukrMKYNKQbSWMjfbWwlf7FEzKRAvXXDRgKDwmVrEVWmTS1c5WLrd9vdgdwu/0" class="user-image" alt="User Image">
              <span class="hidden-xs">顾杰勇</span>
            </a>
            <ul class="dropdown-menu">
              <!-- User image -->
              <li class="user-header">
                <img src="http://wx.qlogo.cn/mmopen/F3xiaBEkH1VJ1Bv7yBx9dMbMB14LtknWsBEl4pukrMKYNKQbSWMjfbWwlf7FEzKRAvXXDRgKDwmVrEVWmTS1c5WLrd9vdgdwu/0" class="img-circle" alt="User Image">

                <p>
                  Alexander Pierce - Web Developer
                  <small>Member since Nov. 2012</small>
                </p>
              </li>
             
              <!-- Menu Footer-->
              <li class="user-footer">
                <div class="pull-left">
                  <a href="#" class="btn btn-default btn-flat">更改密码</a>
                </div>
                <div class="pull-right">
                  <a href="#" id="safeLoginOut" class="btn btn-default btn-flat">安全退出</a>
                </div>
              </li>
            </ul>
          </li>
          <!-- Control Sidebar Toggle Button -->
         <!--  <li>
            <a href="#" data-toggle="control-sidebar"><i class="fa fa-gears"></i></a>
          </li> -->
        </ul>
      </div>
    </nav>
  </header>
  <!-- Left side column. contains the logo and sidebar -->
  <aside class="main-sidebar">
    <!-- sidebar: style can be found in sidebar.less -->
    <section class="sidebar">
      <!-- Sidebar user panel -->
      <div class="user-panel">
        <div class="pull-left image">
          <img src="http://wx.qlogo.cn/mmopen/F3xiaBEkH1VJ1Bv7yBx9dMbMB14LtknWsBEl4pukrMKYNKQbSWMjfbWwlf7FEzKRAvXXDRgKDwmVrEVWmTS1c5WLrd9vdgdwu/0" class="img-circle" alt="User Image">
        </div>
        <div class="pull-left info">
          <p>Alexander Pierce</p>
          <a href="#"><i class="fa fa-circle text-success"></i> Online</a>
        </div>
      </div>
     
      <!-- /.search form -->
      <!-- sidebar menu: : style can be found in sidebar.less -->
      <ul class="sidebar-menu" id="main_menu">
        <li class="header">系统功能主菜单</li>
       
  	</ul>
    </section>
    <!-- /.sidebar -->
  </aside>
  <!-- jQuery 2.2.3 -->
  <script src="http://res.qdevelop.cn/AdminLTE/plugins/jQuery/jquery-2.2.3.min.js"></script>
  <script type="text/javascript"> 
   var domain='<%=request.getContextPath()%>';
   $(function () {
   		$('#safeLoginOut').click(function(){
   			if(confirm('是否确定退出登陆？')){
   				$.ajax({url:domain+'/sys/auth/loginout'
   					,success : function(r) {
   					window.location.reload();
   				}});
   			}
   		});
   		$.ajax({
			url:'<%=request.getContextPath()%>/sys/auth/menu',
			success : function(r) {
				if (r.tag == 0) {
					var rb = r.data.result;
					var html = '<li class="header">系统功能主菜单</li>';
					for (var i = 0; i < rb.length; i++) {
						html += '<li class="treeview '+(i==0?"active":"")+'"><a href="#"><i class="fa '+rb[i]['menu_icon']+'"></i> <span>'+rb[i]['menu_name']+'</span>'
					          +'<span class="pull-right-container"><i class="fa fa-angle-left pull-right"></i></span>'
					          +'</a><ul class="treeview-menu">';
				          if(typeof rb[i]['childs'] != 'undefined'){
				        	  var child = rb[i]['childs'];
				        	  for (var j = 0; j < child.length; j++) {
				        		  html += '<li><a href="'+child[j]['menu_link']+'"><i class="fa fa-circle-o"></i> '+child[j]['menu_name']+'</a></li>';
				        	  }
				          }
				          html += '</ul></li>'     
					}
					$('#main_menu').html(html);
				}else{
					alert(r.errMsg);
				}
			}
		});
   });
   </script>
  