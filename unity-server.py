from http.server import SimpleHTTPRequestHandler, HTTPServer
import os

class GzipHandler(SimpleHTTPRequestHandler):
    def do_GET(self):
        if self.path.endswith('.gz'):
            self.send_response(200)
            self.send_header('Content-Encoding', 'gzip')
            self.send_header('Content-Type', 'application/wasm')
            self.end_headers()
            with open(os.getcwd() + self.path, 'rb') as f:
                content = f.read()
                self.wfile.write(content)
        else:
            super().do_GET()

def run(server_class=HTTPServer, handler_class=GzipHandler, port=8000):
    server_address = ('', port)
    httpd = server_class(server_address, handler_class)
    print(f'Starting server on port {port}...')
    httpd.serve_forever()

if __name__ == '__main__':
    run()
