SonarQube Ruby Plugin
=================
## Historical reference
I forked it off of [Godday branch](https://github.com/GoDaddy-Hosting/ruby-sonar-plugin) and upgraded the code to make it compliant with the newest versions of SonarQube and its API. Currently, functional and integration testing is not available--it needs to be added.

## Description / Features
The plugin enables analysis of Ruby projects within SonarQube (most recently tested against version 6.4 and 6.5 )


<hr>

# The following might be obsolete

Currently the plugin captures basic metrics (Lines of Code, Number of classes and packages, Comment percentage), 
complexity on each file, and a visual line-by-line code coverage report.

It relies on well-known external tools: [SimpleCov](https://github.com/colszowka/simplecov), [SimpleCov-RCov](https://github.com/fguillen/simplecov-rcov) and [Metric_Fu](https://github.com/metricfu/metric_fu/)

## Install
Download the plugin into the SONARQUBE_HOME/extensions/plugins directory

## Usage
Make sure the property sonar.language is set to ruby: `sonar.language=ruby` in the sonar-project.properties file

##### Code Coverage
In order for the plugin to report on code coverage, the ruby project needs to be using [simplecov-rcov](https://github.com/fguillen/simplecov-rcov) 
to generate a coverage report when you run your tests/specs, please see the gem's homepage [here](https://github.com/fguillen/simplecov-rcov) for installation
and usage instructions.  
**Important:** Do not change the output directory for the simplecov-rcov report, leave it as default, or code coverage will not be reported.

##### Code Complexity
In order for the plugin to report on code complexity, [metric_fu](https://github.com/metricfu/metric_fu/) needs to be ran against the ruby project,
which will generate a metric report. Please see the gem's homepage [here](https://github.com/metricfu/metric_fu/) for installation and usage instructions.  
**Important:** metric_fu reports on more than just code complexity, however we still recommend to use the metric_fu command: `metric_fu -r`
this will run all metrics. At the very least, Saikuro/Cane and Hotspots metrics need to be ran for complexity to be reported.

## Future Plans
* Code Duplication
* Code Violations

## Giving Credit
The github project [pica/ruby-sonar-plugin](https://github.com/pica/ruby-sonar-plugin), is where the ruby-sonar-plugin started, rather than reinvent the wheel, we thought it better to enhance it.
We used that plugin as a starting point for basic stats, then, updated the references to their latest versions and added additional metrics like line-by-line code coverage and code complexity.

We referenced the [javascript sonar plugin](https://github.com/SonarCommunity/sonar-javascript) and the [php sonar plugin](https://github.com/SonarCommunity/sonar-php) for complexity and coverage implementation.
Our complexity sensor and code coverage sensor borrow heavily from the javascript plugin's equivalent sensors.

## Tool Versions
This plugin has been tested with the following dependency versions
* [SonarQube](http://www.sonarqube.org/downloads/) 4.5.5 LTS
* SonarQube Runner 2.3 (or newer)
* metric_fu gem version 4.12.0 (latest at time of edit)
* simplecov 0.8.2
* simplecov-rcov 0.2.3
