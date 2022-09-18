const backendURL: string = "http://localhost:3000";

interface Horoszkop {
    horoszkop: string;
    csillagjegy: string;
}

interface Horoszkopok {
    tenyleges: number;
    horoszkopok: Horoszkop[];
}

(async () => {
    const csillagjegyek: string[] = await (await fetch(`${backendURL}/csillagjegyek`)).json();

    document.getElementById("csillagjegyek")!.innerHTML =
        csillagjegyek
            .map(csillagjegy => `<option value = '${csillagjegy}'>${csillagjegy}</option>`)
            .join("\n");

    document.getElementById("horoszkopok-generalasa")!
        .onclick = async () => {
            document.body.style.cursor = "wait";

            const csillagjegy: string = (document.getElementById("csillagjegyek")! as HTMLInputElement).value;
            const horoszkopok: Horoszkopok =
                (await (await fetch(`${backendURL}/horoszkopok-generalasa/${csillagjegy}`)).json());

            document
                .getElementById("lehetoseg-hint")!
                .style
                .visibility = "visible";

            horoszkopok.horoszkopok.forEach((horoszkop, index) => {
                document
                    .getElementById(`lehetoseg${index + 1}`)!
                    .style
                    .visibility = "visible";

                document
                    .getElementById(`horoszkop${index + 1}`)!
                    .removeAttribute("style");

                document
                    .getElementById(`lehetoseg${index + 1}`)!
                    .onclick = async () => {
                        document
                            .getElementById(`horoszkop${index + 1}`)!
                            .innerHTML += ` <b>(${horoszkop.csillagjegy})</b>`;

                        document
                            .getElementById(`horoszkop${index + 1}`)!
                            .style
                            .background = "#FFCCCB";

                        document
                            .getElementById(`horoszkop${horoszkopok.tenyleges + 1}`)!
                            .style
                            .background = "#90EE90";

                        await fetch(`${backendURL}/statisztika-hozzaadasa/${horoszkop.csillagjegy}/${csillagjegy}/${horoszkopok.tenyleges == index}`);
                    }

                document
                    .getElementById(`horoszkop${index + 1}`)!
                    .innerHTML = `<b>${index + 1}.</b> ${horoszkop.horoszkop}`;
            });

            document.body.style.cursor = "auto";
        };
})();


// #90EE90