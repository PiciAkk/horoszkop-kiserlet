"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
const backendURL = "http://minecraft.veddvelem.hu:3000";
(() => __awaiter(void 0, void 0, void 0, function* () {
    const csillagjegyek = yield (yield fetch(`${backendURL}/csillagjegyek`)).json();
    document.getElementById("csillagjegyek").innerHTML =
        csillagjegyek
            .map(csillagjegy => `<option value = '${csillagjegy}'>${csillagjegy}</option>`)
            .join("\n");
    document.getElementById("horoszkopok-generalasa")
        .onclick = () => __awaiter(void 0, void 0, void 0, function* () {
        document.body.style.cursor = "wait";
        const csillagjegy = document.getElementById("csillagjegyek").value;
        const horoszkopok = (yield (yield fetch(`${backendURL}/horoszkopok-generalasa/${csillagjegy}`)).json());
        document
            .getElementById("lehetoseg-hint")
            .style
            .visibility = "visible";
        horoszkopok.horoszkopok.forEach((horoszkop, index) => {
            document
                .getElementById(`lehetoseg${index + 1}`)
                .style
                .visibility = "visible";
            document
                .getElementById(`horoszkop${index + 1}`)
                .removeAttribute("style");
            document
                .getElementById(`lehetoseg${index + 1}`)
                .onclick = () => __awaiter(void 0, void 0, void 0, function* () {
                document
                    .getElementById(`horoszkop${index + 1}`)
                    .innerHTML += ` <b>(${horoszkop.csillagjegy})</b>`;
                document
                    .getElementById(`horoszkop${index + 1}`)
                    .style
                    .background = "#FFCCCB";
                document
                    .getElementById(`horoszkop${horoszkopok.tenyleges + 1}`)
                    .style
                    .background = "#90EE90";
                yield fetch(`${backendURL}/statisztika-hozzaadasa/${horoszkop.csillagjegy}/${csillagjegy}/${horoszkopok.tenyleges == index}`);
            });
            document
                .getElementById(`horoszkop${index + 1}`)
                .innerHTML = `<b>${index + 1}.</b> ${horoszkop.horoszkop}`;
        });
        document.body.style.cursor = "auto";
    });
}))();
// #90EE90
