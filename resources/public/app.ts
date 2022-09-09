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
        csillagjegyek.map(csillagjegy => `<option value = '${csillagjegy}'>${csillagjegy}</option>`)
                     .join("\n");

    document.getElementById("horoszkopok-generalasa")!
        .onclick = async () => {
            document.body.style.cursor = "wait";

            const csillagjegy: string = (document.getElementById("csillagjegyek")! as HTMLInputElement).value;
            const horoszkopok: Horoszkopok = 
                (await (await fetch(`${backendURL}/horoszkopok-generalasa/${csillagjegy}`)).json());

            console.log(horoszkopok.tenyleges + 1);

            document.getElementById("lehetoseg-hint")!
                .style
                .visibility = "visible";

            horoszkopok.horoszkopok.forEach((horoszkop, index) => {
                document.getElementById(`lehetoseg${index+1}`)!
                        .style
                        .visibility = "visible";

                document.getElementById(`horoszkop${index+1}`)!
                        .innerHTML = `${index+1}. ${horoszkop.horoszkop}`;
            });

            document.body.style.cursor = "auto";
        };
})();