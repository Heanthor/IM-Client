# IM-Client 

My IM client and server. The server is run on a separate connection on an Amazon Linux server, and clients can be ran from anywhere. A username and password is required to join, but once created, the name is stored indefinitely on the server, to be used whenever. No special permissions are associated with an account at this point.

Correctly authenticates users, turning them away if the username or password is wrong, and correctly registers users as long as their desired screen name is not already being used. Has basic tabbed messages, so that your conversations are kept separate between any online users. A group chat mode is coming soon.

Uses my 'Bloom Filter' and 'Login PoC' projects.

## Server Commands

* ```connected``` - Lists the IPs of the users currently connected to the server.
* ```users``` - Prints the usernames of registered accounts (contained in users.ser).
* ```stats``` - Prints usage and uptime statistics about the server.
* ```help``` - Displays list of commands
* ```quit``` - Stops the server, forcing all clients to disconnect.

## Issues

* Connecting with the same username as someone already logged in breaks the server. Don't share your password?
* Rare/strange Swing bugs, mostly dealing with the user list.
* Server doesn't know how to handle force-quitting of clients, will desync the user list and cause problems for everyone once one person disconnects unnaturally.
