# myCloud
上汽大众云
## Phase 1 总结
---
+ 登录、注册、修改密码、注销功能已完成
+ 自动登录已经完成，通过sharedPreference完成
+ 已与本地的wamp server对接，可以在安卓模拟器上正常运行
+ 本地数据库也已建好，可以正常的存取数据
+ 主界面UI采用material design
+ 小bug：主界面的导航界面不能动态显示用户名与邮箱


## Phase 2 总结

---

+ 导航界面可以动态显示用户名与邮箱
+ 主界面（分条查看问题单）、新建问题单、问题单详情、新建原因、查看原因、新建措施、查看措施界面已做好
+ 问题单根据进展状态分为A、B、C、D四个阶段，用在主界面排序后以不同的颜色标识，提醒用户及时处理问题
+ 照片上传下载的原理：将照片转为Base64编码的字节数组，利用Volley的StringRequest上传入服务器；服务器存储好图片，生成一个url，请求图片时，服务器返回url，App通过Glide直接访问url加载出图片
+ 暂时只能上传一张图片，某些界面存在操作顺序的限制

