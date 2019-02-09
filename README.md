# Trello_quora
Repository For Group Project
The following API endpoints are implemented in 'UserController' class:

1. signup - "/user/signup"

 This endpoint is used to register a new user in the Quora Application.

2. signin - "/user/signin"

This endpoint is used for user authentication. The user authenticates in the application and after successful authentication, JWT token is given to a user.

3. signout - "/user/signout"

This endpoint is used to sign out from the Quora Application. The user cannot access any other endpoint once he is signed out of the application.

The following API endpoints areimplemented in 'CommonController' class:

1. userProfile - "/userprofile/{userId}"

This endpoint is used to get the details of any user in the Quora Application. This endpoint can be accessed by any user in the application.

The following API endpoints are implemented in 'AdminController' class:

1. userDelete - "/admin/user/{userId}"

This endpoint is used to delete a user from the Quora Application. Only an admin is authorized to access this endpoint.




