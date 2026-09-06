Service Ownership

## User Service

The User Service owns all User-related data.

Responsibilities:
- Create users
- Retrieve users
- Update users
- Delete users
- Maintain User Service database

The User Service is the owner of User Data.

---

## Order Service

The Order Service owns all Order-related data.

Responsibilities:
- Create orders
- Retrieve orders
- Retrieve orders for a user
- Update order status
- Cancel orders
- Delete orders
- Maintain Order Service database

The Order Service is the owner of Order Data.

---

## Service Boundary

The services must maintain clear ownership boundaries.

```text
User Service
     |
     | Owns
     v
  User Data


Order Service
     |
     | Owns
     v
 Order Data
Database Access Rule
The Order Service must NOT directly access the User Service database.
The Order Service can communicate with the User Service through its APIs when User Service information is required.
Order Service
      |
      | REST API
      v
User Service
      |
      v
User Database
The Order Service has its own database and repository for Order data.
The User Service has its own database and repository for User data.

3. Press **Ctrl + S**.
