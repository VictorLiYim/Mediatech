import { defineStore } from "pinia";
import { ref, computed } from "vue";
import * as usersApi from "@/api/users";

export const useUserStore = defineStore("user", () => {
    const currentUser = ref(null);

    const isLoggedIn = computed(() => currentUser.value !== null);

    async function login(credentials) {
        currentUser.value = await usersApi.login(credentials);
        return currentUser.value;
    }

    async function register(payload) {
        await usersApi.register(payload);
        return login({ userName: payload.userName, password: payload.password });
    }

    function logout() {
        currentUser.value = null;
    }

    return { currentUser, isLoggedIn, login, register, logout };
});