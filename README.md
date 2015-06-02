# IM-Client 

My IM client and server. The server is run on a separate connection (for now in Eclipse hosted on Amazon ECS), and clients can be ran from anywhere. A username and password is required to join, but once created, the name is stored indefinitely on the server, to be used whenever. No special permissions are associated with an account at this point.

Correctly authenticates users, turning them away if the username or password is wrong, and correctly registers users as long as their desired screen name is not already being used. Sometimes crashes when multiple users are online, but that will be fixed soon. Additionally, a group chat mode where all currently connected chatters can see messages (like IRC) is being added soon.
