# TwitchTracker Scraper

## Overview
TwitchTracker Scraper is a Java-based application designed to scrape and analyze data from TwitchTracker.com. The application scrapes basic details of streamers, all streams associated with a specific streamer, and stores this data in a Microsoft Azure SQL database.

## Features
- **Streamer Data Scraping**: Extracts basic information about Twitch streamers.
- **Stream Data Scraping**: Retrieves details of individual streams per streamer.
- **Data Storage**: Utilizes Microsoft Azure SQL for data persistence.
- **Batch Processing**: Efficient data insertion using Hibernate batch processing.
- **API Testing**: API endpoints tested with Postman for robustness.

## Technology Stack
- **Backend**: Spring Boot (Java)
- **Database**: Microsoft Azure SQL
- **Web Scraping Tool**: Jsoup for Java
- **ORM**: Hibernate with Spring Data JPA

## Installation
1. Clone the repository:

git clone [repository URL]

css

2. Navigate to the project directory and build the project:

cd TwitchTrackerScraper
mvn clean install

csharp


## Configuration
Update `application.properties` with your database credentials and other configurations:
```properties
spring.datasource.url=[Your Database URL]
spring.datasource.username=[Your Username]
spring.datasource.password=[Your Password]
# Other configurations

Running the Application

To start the application, run:

bash

java -jar target/twitchtrackerscraper-1.0-SNAPSHOT.jar

Usage

    Start the application.
    Use Postman or similar tools to test the REST API endpoints.
    The application will automatically scrape data based on predefined intervals.

Contributing

Contributions to the TwitchTracker Scraper are welcome. Please follow these steps to contribute:

    Fork the repository.
    Create a new branch for your feature.
    Commit your changes and open a pull request.

License

This project is licensed under the [LICENSE NAME].
Contact

For any queries, please reach out to [Your Contact Information].
Acknowledgements

    TwitchTracker
    Spring Boot Community
    Contributors and Supporters

css


Replace placeholders like `[repository URL]`, `[Your Database URL]`, `[Your Username]`, `[Your Password]`, `[LICENSE NAME]`, and `[Your Contact Information]` with the actual details of your project.

This README provides a basic outline of your project, including how to set i
