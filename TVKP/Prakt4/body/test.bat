@echo off
echo Running UserController tests...
for /L %%i in (1,1,100) do (
  curl -X POST http://localhost:8080/users -H "Content-Type: application/json" -d "{\"name\": \"Test User %%i\", \"email\": \"user%%i@example.com\"}"
  curl -X GET http://localhost:8080/users
  curl -X PUT http://localhost:8080/users/%%i -H "Content-Type: application/json" -d "{\"name\": \"Updated User %%i\", \"email\": \"updateduser%%i@example.com\"}"
  curl -X DELETE http://localhost:8080/users/%%i
)

echo Running ProductController tests...
for /L %%i in (1,1,100) do (
  curl -X POST http://localhost:8080/products -H "Content-Type: application/json" -d "{\"name\": \"Product %%i\", \"price\": %%i.99}"
  curl -X GET http://localhost:8080/products
  curl -X PUT http://localhost:8080/products/%%i -H "Content-Type: application/json" -d "{\"name\": \"Updated Product %%i\", \"price\": %%i.50}"
  curl -X DELETE http://localhost:8080/products/%%i
)

echo Running OrderController tests...
for /L %%i in (1,1,100) do (
  curl -X POST http://localhost:8080/orders -H "Content-Type: application/json" -d "{\"user\": {\"id\": %%i}, \"products\": [{\"id\": 1}, {\"id\": 2}]}"
  curl -X GET http://localhost:8080/orders
  curl -X PUT http://localhost:8080/orders/%%i -H "Content-Type: application/json" -d "{\"user\": {\"id\": %%i}, \"products\": [{\"id\": 1}, {\"id\": 3}]}"
  curl -X DELETE http://localhost:8080/orders/%%i
)

echo All tests completed.
pause
