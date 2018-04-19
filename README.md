# MediaContentProviderService

Technologies/Tools : Spring mvc 4, Tomcat 8, Java 8, Maven, Eclispe Oxygen.2 Release (4.7.2)
Note: you can use any client to hit this api, i was using postman

This service expose one api:
http://localhost:8080/ContentProviderService/media/get?filter=censoring&level=uncensored

Description: This service reads json data from a url then parse that json using json simple api. it retrieves the correct media content from the url based on the level param passed.
if the value of level is censored, it will return censored media content ,if the value is uncensored, it will return uncensored content in the form of json.

java 8 streams and lambda expression are used to minimize the amount of code.
