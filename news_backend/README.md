# News API
- This is a mock project I created to learn how to work with Kotlin.
- the entire backend was generated with the help of deepseek, and i used it as a hands‑on way to explore kotlin basics, api design, and project structure.
---
#### Below is list of available API endpoints and their responses.
  1. Health Check
  ``` bash
  curl -X GET "http://localhost:8000/health"

  ```


  2. Root Endpoint (API Documentation)

  ``` bash
    curl -X GET "http://localhost:8000/"

  ```

  - Result
      ``` json
    {"message":"Welcome to News API Server","version":"1.0.0","endpoints":{"news":{"create_news":"POST /api/news/","titles_by_category":"GET /api/news/by-category/{category_id}/titles","titles_by_multiple_categories":"POST /api/news/by-multiple-categories/titles","newest_titles":"GET /api/news/newest/titles","news_by_id":"GET /api/news/{news_id}","newest_full":"GET /api/news/newest/full","full_by_category":"GET /api/news/by-category/{category_id}/full"},"health":"/health","docs":"/docs","redoc":"/redoc"}}
      ```
  3. Create a New News Article
  ``` bash
  curl -X POST "http://localhost:8000/api/news/" \
    -H "Content-Type: application/json" \
    -d '{
      "title": "Breaking News: AI Revolution",
      "description": "Artificial Intelligence is transforming industries worldwide...",
      "category_ids": [1, 2],
      "source": "Tech Times",
      "image_id": 1
    }'

  ```
  - Result
      ``` json
      {"success":true,"message":"News article created successfully","data":{"id":3,"title":"Breaking News: AI Revolution","created_at":"2025-12-30T09:07:44"},"timestamp":"2025-12-30T12:37:11.939209"}
      ```

  4. Get All Categories
  ``` bash
   ▶ curl -X GET "http://localhost:8000/api/categories/"
  ```
  - Result
  ``` json
    {"success":true,"message":"Found 8 categories","data":[{"category_id":1,"category_name":"politics"},{"category_id":2,"category_name":"technology"},{"category_id":3,"category_name":"sports"},{"category_id":4,"category_name":"entertainment"},{"category_id":5,"category_name":"business"},{"category_id":6,"category_name":"health"},{"category_id":7,"category_name":"science"},{"category_id":8,"category_name":"world"}],"timestamp":"2025-12-31T12:00:00.000000"}
  ```

  5. Get News Titles by Category ID
  ``` bash
  curl -X GET "http://localhost:8000/api/news/by-category/2/titles?limit=5"

  ```
  - Result
      ``` json
      {"success":true,"message":"Found 1 news titles","data":[{"id":3,"title":"Breaking News: AI Revolution"}],"timestamp":"2025-12-30T12:37:11.939209"}
      ```

  6. Get News Titles by Multiple Category IDs
  ``` bash
  curl -X POST "http://localhost:8000/api/news/by-multiple-categories/titles" \
    -H "Content-Type: application/json" \
    -d '{
      "category_ids": [2, 5, 7],
      "limit_per_category": 3
    }'

  ```
  - Result
      ``` json
    {"success":true,"message":"Found 1 news items","data":[{"id":3,"title":"Breaking News: AI Revolution","categories":[{"category_id":2,"category_name":"technology"}],"timestamp":"2025-12-30T09:07:44.768050","source":"Tech Times"}],"timestamp":"2025-12-30T12:37:11.939209"}
    ```

  7. Get Newest News Titles (All Categories)
  ``` bash
  curl -X GET "http://localhost:8000/api/news/newest/titles?limit=10"

  ```
  - Result
      ``` json

    {"success":true,"message":"Found 3 newest news titles","data":[{"id":3,"title":"Breaking News: AI Revolution"},{"id":2,"title":"Breaking News"},{"id":1,"title":"Breaking News"}],"timestamp":"2025-12-30T12:37:11.939209"}
    ```

  8. Get Full News Article by ID
  ```bash
  curl -X GET "http://localhost:8000/api/news/1"
  ```

  9. Get Newest Full News Articles
  ```bash
  curl -X GET "http://localhost:8000/api/news/newest/full?limit=5"
  ```

  10. Get Full News Articles by Category ID
  ```bash
  curl -X GET "http://localhost:8000/api/news/by-category/2/full?limit=5"
  ```


  ```

  
  2. Root Endpoint (API Documentation)
  
  ``` bash 
    curl -X GET "http://localhost:8000/"

  ```
  
  - Result
      ``` json 
    {"message":"Welcome to News API Server","version":"1.0.0","endpoints":{"news":{"create_news":"POST /api/news/","titles_by_category":"GET /api/news/by-category/{category}/titles","titles_by_multiple_categories":"POST /api/news/by-multiple-categories/titles","newest_titles":"GET /api/news/newest/titles","news_by_id":"GET /api/news/{news_id}","newest_full":"GET /api/news/newest/full","full_by_category":"GET /api/news/by-category/{category}/full"},"health":"/health","docs":"/docs","redoc":"/redoc"}}%   
      ```
  3. Create a New News Article
  ``` bash 
  curl -X POST "http://localhost:8000/api/news/" \
    -H "Content-Type: application/json" \
    -d '{
      "title": "Breaking News: AI Revolution",
      "description": "Artificial Intelligence is transforming industries worldwide...",
      "category": "technology",
      "source": "Tech Times",
      "image_id": 1
    }'
  
  ```
  - Result
      ``` json 
      {"success":true,"message":"News article created successfully","data":{"id":3,"title":"Breaking News: AI Revolution","created_at":"2025-12-30T09:07:44"},"timestamp":"2025-12-30T12:37:11.939209"}%                                                                                                                                                                                            
      ```
  
  4. Get All Categories 
  ``` bash 
   ▶ curl -X GET "http://localhost:8000/api/categories/"
```
  - Result
  ``` json 
    {"success":true,"message":"Found 8 categories","data":[{"category_id":1,"category_name":"politics"},{"category_id":2,"category_name":"technology"},{"category_id":3,"category_name":"sports"},{"category_id":4,"category_name":"entertainment"},{"category_id":5,"category_name":"business"},{"category_id":6,"category_name":"health"},{"category_id":7,"category_name":"science"},{"category

```
  4. Get News Titles by Category
  ``` bash 
  curl -X GET "http://localhost:8000/api/news/by-category/technology/titles?limit=5"
  
  ```
  - Result
      ``` json 
      {"success":true,"message":"Found 1 news titles","data":[{"id":3,"title":"Breaking News: AI Revolution"}],"timestamp":"2025-12-30T12:37:11.939209"}%                                            
      ```
  
  5. Get News Titles by Multiple Categories
  ``` bash 
  curl -X POST "http://localhost:8000/api/news/by-multiple-categories/titles" \
    -H "Content-Type: application/json" \
    -d '{
      "categories": ["technology", "business", "science"],
      "limit_per_category": 3
    }'
  
  ```
  - Result
      ``` json 
    {"success":true,"message":"Found 1 news items","data":[{"id":3,"title":"Breaking News: AI Revolution","category":"technology","timestamp":"2025-12-30T09:07:44.768050","source":"Tech Times"}],"timestamp":"2025-12-30T12:37:11.939209"}%
      ```
  
  6. Get Newest News Titles (All Categories)
  ``` bash 
  curl -X GET "http://localhost:8000/api/news/newest/titles?limit=10"
  
  ```
  - Result
      ``` json 

    {"success":true,"message":"Found 3 newest news titles","data":[{"id":3,"title":"Breaking News: AI Revolution"},{"id":2,"title":"Breaking News"},{"id":1,"title":"Breaking News"}],"timestamp":"2025-12-30T12:37:11.939209"}%   
      ```
  
  7. Get Full News Article by ID ( not implemented yet )
  ```bash  
  curl -X GET "http://localhost:8000/api/news/1"
  ```
  
  
  8. Get Newest Full News Articles ( not implemented yet )
  ```bash 
  curl -X GET "http://localhost:8000/api/news/newest/full?limit=5"
  ```
  
  
  9. Get Full News Articles by Category ( not implemented yet)
  ```  bash
  curl -X GET "http://localhost:8000/api/news/by-category/technology/full?limit=5"
  ```
  
  
