# WeatherDemo

天气应用
1.  接口：http://apis.baidu.com/heweather/weather/free
2.  使用gson解析JSON
3.  数据缓存在数据库中，使用ContentProvider来处理
4.  如果不强制刷新，则使用缓存数据每隔一定时间再刷新一次
