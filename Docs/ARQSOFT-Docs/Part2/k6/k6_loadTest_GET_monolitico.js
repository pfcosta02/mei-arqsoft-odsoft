import http from 'k6/http';
import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';
import papaparse from 'https://jslib.k6.io/papaparse/5.1.1/index.js';
//import { htmlReport } from 'https://raw.githubusercontent.com/benc−uk/k6−reporter/main/dist/bundle.js';

// Test configuration
export let options = {
    thresholds: {
        http_req_failed: ['rate<0.01'], // http errors should be less than 1%
        http_req_duration: ['p(95)<2000'], // 95% of requests should be below 2000ms
    },
    stages: [
        { duration: '10s', target: 50 }, // Ramp-up: 50 users over 10s
        { duration: '120s', target: 50 }, // Hold-up:50 users over 120s
        { duration: '10s', target: 0 },
    ],
};

// Login once before the test starts
export function setup() {
    const loginRes = http.post('http://localhost:8080/api/public/login', JSON.stringify({
        username: "maria@gmail.com",
        password: "Mariaroberta!123"
    }), {
        headers: { 'Content-Type': 'application/json', 'Accept': '*/*' }
    });

    const token = loginRes.headers['Authorization'];
    return { token };
}

// Main test function
export default function (data) {

    const headers = {
        headers: {
            Authorization: `Bearer ${data.token}`,
        },
    };

    // Get: Fetching author
    let getAuthor = http.get('http://localhost:8080/api/authors?name=Ant', headers);
    check(getAuthor, { 'retrieved author': (r) => r.status >= 200 && r.status < 300 });

    sleep(1);
}
