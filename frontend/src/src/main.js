import { createApp } from "vue";
import { createPinia } from "pinia";

import App from "./App.vue";
import router from "./router";

import "vuetify/styles";
import "@mdi/font/css/materialdesignicons.css";
import "./assets/main.css";
import { createVuetify } from "vuetify";
import * as components from "vuetify/components";
import * as directives from "vuetify/directives";

const vuetify = createVuetify({
    components,
    directives,
    theme: {
        defaultTheme: "light",
        themes: {
            light: {
                dark: false,
                colors: {
                    primary: "#7C7891",
                    "primary-darken-1": "#5F5C74",
                    secondary: "#B1AEC1",
                    background: "#F2F0EB",
                    surface: "#FFFFFF",
                    "on-primary": "#FFFFFF",
                    "on-secondary": "#20201E",
                    error: "#B3261E",
                    success: "#4C7A51",
                    warning: "#B58A2E",
                    info: "#55534E",
                },
            },
        },
    },
    defaults: {
        VBtn: { class: "btn-pill", elevation: 0 },
        VCard: { rounded: "lg" },
        VTextField: { rounded: "lg" },
    },
});

createApp(App).use(createPinia()).use(router).use(vuetify).mount("#app");