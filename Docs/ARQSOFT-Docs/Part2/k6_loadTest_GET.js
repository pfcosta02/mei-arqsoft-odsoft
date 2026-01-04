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

// export function handleSummary(data) {
//     return {
//         "/tests/summary_k6_loadTest_GET_Store.html": htmlReport(data),
//     };
// }

// Login once before the test starts
export function setup() {
    const loginRes = http.post('http://localhost:8089/api/public/login', JSON.stringify({
        username: "maria@gmail.com",
        password: "Mariaroberta!123"
    }), {
        headers: { 'Content-Type': 'application/json', 'Accept': '*/*' }
    });

    const token = loginRes.json('Authorization');
    return { token };
}

// Main test function
export default function () {
    // Create: Adding a new book
    // let createPayload = JSON.stringify({
    //     authors: [{ firstName: "John" + Math.random(), lastName: "Doe" }],
    //     description: "desc",
    //     isbn: "123-1234567890" + Math.random(),
    //     publisher: "My publisher",
    //     title: "My book"
    // });

    // let createRes = http.post('http://rest-books:8080/api/books', createPayload, { headers: { 'Content-Type': 'application/json', 'Accept': '*/*' } });
    // check(createRes, { 'created book': (r) => r.status === 201 });

    // // Get: Fetching a specific book
    // let getRes = http.get('http://rest-books:8080/api/books/978-0321356680');
    // check(getRes, { 'retrieved book': (r) => r.status === 200 });

    // // Update: Updating book info using data from the CSV
    // const book = csvData[Math.floor(Math.random() * csvData.length)];
    // let updatePayload = JSON.stringify({
    //     authors: [{ firstName: book.author_first_name, lastName: book.author_last_name }],
    //     description: book.description,
    //     isbn: book.isbn,
    //     publisher: book.publisher,
    //     title: book.title
    // });

    // let updateRes = http.put('http://rest-books:8080/api/books/${book.isbn}', updatePayload, { headers: { 'Content-Type': 'application/json' } });
    // check(updateRes, { 'updated book': (r) => r.status === 200 });

    // // Patch: Updating the book's description
    // let patchPayload = JSON.stringify("new description");

    // let patchRes = http.patch('http://rest-books:8080/api/books/978-1491900864', patchPayload, { headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' } });
    // check(patchRes, { 'patched book description': (r) => r.status === 200 });

    // // Delete: Removing a book
    // let deleteRes = http.del('http://rest-books:8080/api/books/978-1617292545');
    // check(deleteRes, { 'deleted book': (r) => r.status === 204 });

    // Get All: Fetching all books
    let getAuthor = http.get('http://localhost:8083/api/authors?Robert');
    check(getAuthor, { 'retrieved author': (r) => r.status >= 200 && r.status < 300 });

    sleep(1);
}
