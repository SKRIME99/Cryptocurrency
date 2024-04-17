# CryptoInfo Web Application

CryptoInfo is a simple web application that allows users to retrieve information about cryptocurrencies using the CoinCap API. Users can enter the name of a cryptocurrency, and the application will display relevant data in JSON format.

## Features

- **Cryptocurrency Data Retrieval**: Users can enter the name of a cryptocurrency in the provided input field.
- **CoinCap API Integration**: The application integrates with the CoinCap API to fetch real-time data about cryptocurrencies.
- **Display JSON Data**: The fetched cryptocurrency data is displayed in JSON format on the webpage.

## Technologies Used

- Java Spring Boot: Backend framework for building the RESTful API.
- HTML, CSS, JavaScript: Frontend technologies for creating the user interface and handling user interactions.
- CoinCap API: External API for fetching cryptocurrency data.

## Setup Instructions

1. **Clone the Repository**: Clone this repository to your local machine using the following command:
    ```
    git clone https://github.com/yourusername/cryptoinfo.git
    ```

2. **Navigate to Project Directory**: Change your current directory to the project directory:
    ```
    cd cryptoinfo
    ```

3. **Build and Run the Application**: Use Maven or Gradle to build and run the Spring Boot application:
    ```
    mvn spring-boot:run
    ```
    or
    ```
    ./gradlew bootRun
    ```
4. **Access the Application**: Open your web browser and navigate to `http://localhost:8080` to access the CryptoInfo web application.

## Usage

1. Enter the name of a cryptocurrency in the provided input field.
2. Click the "See JSON" button to fetch data about the entered cryptocurrency.
3. The application will display the fetched cryptocurrency data in JSON format on the webpage.


