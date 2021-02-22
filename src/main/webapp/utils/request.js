import axios from '../js/axios.min'
import config from '../js/settings'

const request = axios.create({
    baseUrl: config.baseUrl,
    timeout: config.timeout
})