# IM-Client 

My IM client and server. The server is run on a separate connection on an Amazon Linux server, and clients can be ran from anywhere. A username and password is required to join, but once created, the name is stored indefinitely on the server, to be used whenever. No special permissions are associated with an account at this point.

Correctly authenticates users, turning them away if the username or password is wrong, and correctly registers users as long as their desired screen name is not already being used. Has basic tabbed messages, so that your conversations are kept separate between any online users. A group chat mode is coming soon.

Uses my 'Bloom Filter' and 'Login PoC' projects.
