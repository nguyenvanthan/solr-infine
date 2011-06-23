Sunspot is a Solr client written in Flex 3.0. Solr is a powerful open source search server based on Lucene. Generally speaking, Solr indexes documents via XML input and accepts queries for its indexed data via HTTP GET and POST requests. You can find more information on solr at apache.org.

License
The software is currently licensed under the Apache Software Foundation which means "it allows use of the source code for the development of free and open source software as well as proprietary software". Many prominent websites already use Solr including netflix.com and cnet.com. If you are reading this then you probably have an idea what Solr is and and may be interested in this client.

This release of Sunspot includes a new progress bar indicator, the ability to post data, an xml configuration file, and a basic help tab. 

Installation
1.Once you've downloaded Sunspot, unzip the files and place them on the server you wish to access the client from. I usually place the folder in the webapps/solr folder, but you can place them wherever you need as long as they are available to Sunspot. See your Tomcat documentation for help. 
2.After you've unzipped the files, you will need to tell Sunspot about the Solr server or proxy server you are using and the fields that you want to be available in the client. To add the servers, you will need to edit the config.xml file which comes with the Sunspot package. This XML fie is read by the client upon starting. There are 4 main sections you will need to modify: 
1.Hosts: the hosts node populates the Solr hosts available to your client. Here is an example:

<hosts>
  <host>http://www.mysolrserver1.com:8080/solr/</host>
  <host>http://www.mysolrserver2:8080/solr/</host> 
</hosts>
Add all the hosts you required in this list. Please note that you will need to set up the crossdomain.xml file for clients that are not running on the same server. 

2.Fields: The fields node tells Sunspot which fields are available to client. In the same fashion as the hosts node above, enter the field data from your solr schema. 
<fields>
  <field name="score" width="25" status="true" /> 
  <field name="id" width="25" status="true" /> 
  <field name="category" width="20" status="true" /> 
</fields>

The width determines the default column width of the search results in the datagrid. Status and be used to disable the field without removing it from the config file. 

3.Templates: the templates node allows you to add custom templates to the Post tab. For every template you have in your config.xml schema a new button will be added to your Post tab. You if click on one of these buttons, the template will be place in the post entry area. If you use templates values in the form of [value], Sunspot can replace this value with the currently selected data grid item. Here is an example template: 
<template id="2" name="Update">
  <add>
   <doc>
    <field name="id">[id]</field> 
    <field name="url"><![CDATA[ [url]]]> 32;</field>
    <field name="created_date">[created_date]</field> 
    <field name="title"><![CDATA[ [title]]]> 60;/field>
  </doc>
 </add>
</template> 

Pointing Solr to the config.xml file
The solrConfigUrl variable is a string containing the path to the xml file which has your config.xml. You set this variable in the html file which contains the client. By default, this file is solrClient.html. Here is an excerpt from the file:

var solrConfigUrl="http://www.mysolrserver1.com:8080/solr/solrClient/config.xml";Sunspot will read this file immediately upon startup. 

And lastly, point your browser to the sunspot.html file.
See www.reefkeysoftware.com for help.