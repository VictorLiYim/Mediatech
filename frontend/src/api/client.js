import axios from "axios";

export const userApi = axios.create({
    baseURL: "http://localhost:8083",
    headers: { "Content-Type": "application/json" },
});

export const bookApi = axios.create({
    baseURL: "http://localhost:8081",
    headers: { "Content-Type": "application/json" },
});

export const eventApi = axios.create({
    baseURL: "http://localhost:8082",
    headers: { "Content-Type": "application/json" },
});

/**
 * Extrait un message lisible depuis une erreur axios.
 * Le backend renvoie un ApiError JSON via le GlobalExceptionHandler.
 */
export function extractErrorMessage(error, fallback = "Une erreur est survenue") {
    return error.response?.data?.message ?? error.message ?? fallback;
}