# Teams Plugin for Graylog

[![Build Status](https://travis-ci.org/hidapple/graylog-plugin-teams.svg?branch=master)](https://travis-ci.org/hidapple/graylog-plugin-teams.svg?branch=master)


A Graylog event notification plugin for sending [Microsoft Teams](https://products.office.com/en-us/microsoft-teams/group-chat-software) MessageCard post.

**Required Graylog version:** 3.1.0 and later  
\* In case your Graylog version is 3.1.0 - 3.1.2, please use `graylog-plugin-teams` version `2.0.0`.  
\* In case your Graylog version is lower than 3.1.0, you can still use `graylog-plugin-teams` version `1.x.x`.  

Installation
------------

[Download the plugin](https://github.com/hidapple/graylog-plugin-teams/releases)
and place the `.jar` file in your Graylog plugin directory. The plugin directory
is the `plugins/` folder relative from your `graylog-server` directory by default
and can be configured in your `graylog.conf` file.

Restart `graylog-server` and you are done.

Usage
-----

#### 1. Publish Teams incoming webhook
First of all, you need to publish your Teams incoming webhook. See [Microsoft docs](https://docs.microsoft.com/en-us/microsoftteams/platform/concepts/connectors/connectors-using)
to know detail and how to publish your Teams incoming webhook.

#### 2. Create Graylog notification
Create Graylog notification and choose `Microsoft Teams Notification` as Notification type.

#### 3. Configure Microsoft Teams Notification
Input your Teams incoming webhook published at #1 and fill out other configurations. Here is a screenshot of configuration example.

![Teams notification configuraiton](https://github.com/hidapple/graylog-plugin-teams/blob/master/img/configuration.png)

#### 4. Create Graylog Event Definitions
Create Graylog Event definition and set Microsoft Teams Notification you created at #3 as its Notification.

#### 5. Receive notification
You will receive notification message like below.

![Teams notification message](https://github.com/hidapple/graylog-plugin-teams/blob/master/img/message.png)

Contribution
------------

1. Fork the repository (https://github.com/hidapple/graylog-plugin-teams/fork)
1. Create your feature branch
1. Commit your changes
1. Rebase your local changes against the master branch
1. Make sure your code can be packaged by `mvn` without any errors
1. Create a new Pull Request

Getting development started
---------------------------

This project is using Maven 3 and requires Java 8 or higher.

* Clone this repository.
* Run `mvn package` to build a JAR file.
* Optional: Run `mvn jdeb:jdeb` and `mvn rpm:rpm` to create a DEB and RPM package respectively.
* Copy generated JAR file in target directory to your Graylog plugin directory.
* Restart the Graylog.

Plugin Release
--------------

We are using the maven release plugin:

```
$ mvn release:prepare
[...]
$ mvn release:perform
```

This sets the version numbers, creates a tag and pushes to GitHub. Travis CI will build the release artifacts and upload to GitHub automatically.

License
-------

[GNU General Public License 3.0](https://github.com/hidapple/graylog-plugin-teams/blob/master/LICENSE)

Author
------

[Shohei Hida](https://github.com/hidapple)
