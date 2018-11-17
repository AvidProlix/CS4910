# -*- coding: utf-8 -*-
import SocketServer

class MyTCPHandler(SocketServer.BaseRequestHandler):
    """
    The request handler class for our server.

    It is instantiated once per connection to the server, and must
    override the handle() method to implement communication to the
    client.
    """

    def handle(self):
        # self.request is the TCP socket connected to the client
        self.data = self.request.recv(1024).strip()
        print "{} wrote:".format(self.client_address[0])
        data = self.data
        print data
        # just send back the same data, but upper-cased
        # self.request.sendall(self.data.upper())

if __name__ == "__main__":
	# Host ip is the localhost outer ip. Android must match this IP verbatum (no DNS currently)
    HOST, PORT = "192.168.0.28", 7800

    # Create the server, binding to localhost on port 7800
    server = SocketServer.TCPServer((HOST, PORT), MyTCPHandler)

    # Activate the server; this will keep running until you
    # interrupt the program with Ctrl-C
    server.serve_forever()