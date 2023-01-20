// this file contains configurations for dev proxy

const DEV_PROTOCOL = "http";
const DEV_HOST = "localhost:8080";

module.exports = {
    '/rest': {
        target: "https://dev-mapstore2.geosolutionsgroup.com/mapstore",
        secure: false,
        headers: {
            host: "dev-mapstore2.geosolutionsgroup.com"
        }
    },
    '/pdf': {
        target: "https://dev-mapstore2.geosolutionsgroup.com/mapstore",
        secure: false,
        headers: {
            host: "dev-mapstore2.geosolutionsgroup.com"
        }
    },
    '/mapstore/pdf': {
        target: "https://dev-mapstore2.geosolutionsgroup.com",
        secure: false,
        headers: {
            host: "dev-mapstore2.geosolutionsgroup.com"
        }
    },
    '/proxy': {
        target: "http://localhost:8082/",
        secure: false,
        headers: {
            host: "dev-mapstore2.geosolutionsgroup.com"
        }
    },
    '/docs': {
        target: "http://localhost:8081",
        pathRewrite: {'/docs': '/mapstore/docs'}
    },
    '/signalement': {
        target: "http://localhost:8082",
        pathRewrite: {'/signalement': '/'},
        headers: {
            host: "localhost"
        }   
    }
};
