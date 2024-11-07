//const http = require('http');
import * as http from 'http';
//const redis = require('redis');
import * as redis from 'redis';
import { readFileSync } from 'fs';

// Client for READ
const client = redis.createClient({
    // 'host': 'redis'
    // Read password from file /etc/redis-passwd/passwd
    url: `redis://:${readFileSync('/etc/redis-passwd/passwd')}@redis:6379`
});
client.on('error', err => console.log('Redis Client Error', err));
await client.connect();

// Client for WRITE
const clientWrite = redis.createClient({
    // For master redis (write) we must create separate client with 'host' : 'redis-0.redis'
    // Read password from file /etc/redis-passwd/passwd
    url: `redis://:${readFileSync('/etc/redis-passwd/passwd')}@redis-0.redis:6379`
});
clientWrite.on('error', err => console.log('Redis ClientWrite Error', err));
await clientWrite.connect();

// Init Redis DB
const key = 'journal-key';
await clientWrite.set(key, '[{ "name" : "Frist Name" }]');

// Request handler for HTTP Server
const requestHandler = async (request, response) => {

    console.log(request.url);

    if (!request.url.startsWith('/api')) {
        response.writeHead(404);
        response.end('Not found');
        return;
    }
    if (request.method != 'GET' && request.method != 'POST') {
        response.writeHead(400);
        response.end('Unsupported method.');
        return;
    }

    const value = await client.get(key);
    console.log(`Get data: '${value}'`);
    const journals = value ? JSON.parse(value) : [];
    const journalsToShow = Array.from(journals).slice(0, process.env.JOURNAL_ENTRIES);
    if (request.method == 'GET') {
        response.writeHead(200);
        response.end(JSON.stringify(journalsToShow));
    }
    // curl -X POST -H "Content-Type: application/json" -d '{"name": "John"}' Some-URL
    if (request.method == 'POST') {
        try {
            let body = [];
            request.on('data', async (chunk) => {
                body.push(chunk);
            }).on('end', async () => {
                body = Buffer.concat(body).toString();
                const msg = JSON.parse(body);
                journals.push(msg);
                // Write combined value for key: $key
                await clientWrite.set(key, JSON.stringify(journals));
                console.log(`Wrote data: '${JSON.stringify(journals)}'`);
                response.writeHead(200);
                response.end(JSON.stringify(journals));
            });
        } catch (err) {
            response.writeHeader(500);
            response.end(err.toString());
            return;
        }
    }

    return;
}

// HTTP Server
const port = 8080;
const server = http.createServer(requestHandler);

server.listen(port, (err) => {
  if (err) {
    return console.log('could not start server', err);
  }

  console.log('api server up and running.');
})