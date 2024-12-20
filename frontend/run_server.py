#!/usr/bin/env python3
"""
Very simple HTTP server in python for logging requests
Usage::
    ./server.py [<port>]
"""
from http.server import BaseHTTPRequestHandler, HTTPServer, SimpleHTTPRequestHandler
import logging
from urllib.parse import parse_qs
import re

def safeget(dct, *keys):
    for key in keys:
        try:
            dct = dct[key]
        except KeyError:
            return None
    return dct


class S(SimpleHTTPRequestHandler):
    def _set_response(self):
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()

    def do_GET(self):
        logging.info("GET request,\nPath: %s\nHeaders:\n%s\n", str(self.path), str(self.headers))
        super().do_GET()

    def do_POST(self):
        content_length = int(self.headers['Content-Length']) # <--- Gets the size of data
        post_data = self.rfile.read(content_length) # <--- Gets the data itself
        logging.info("POST request,\nPath: %s\nHeaders:\n%s\n\nBody:\n%s\n",
                str(self.path), str(self.headers), post_data.decode('utf-8'))

        post_data_fields = parse_qs(post_data.decode('utf-8'))


        self._set_response()

        if (re.match("^\/register", self.path) and
            safeget(post_data_fields, "username", 0) == "admin@domain.com" and
            safeget(post_data_fields, "password", 0) == "rainbow") :
            self.wfile.write("""
                <h1>Congrats, you succeeded to submit the correct data</h1>
            """.format(self.path, post_data.decode('utf-8')).encode('utf-8'))
        else:
            self.wfile.write("""
                <h1>Error : Bad Request</h1>
                
                POST request for {}<br>
                Body : <br>
                {}
            """.format(self.path, post_data.decode('utf-8')).encode('utf-8'))

def run(server_class=HTTPServer, handler_class=S, port=8000):
    logging.basicConfig(level=logging.INFO)
    server_address = ('', port)
    httpd = server_class(server_address, handler_class)
    logging.info('Starting httpd...\n')
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        pass
    httpd.server_close()
    logging.info('Stopping httpd...\n')

if __name__ == '__main__':
    from sys import argv

    if len(argv) == 2:
        run(port=int(argv[1]))
    else:
        run()
