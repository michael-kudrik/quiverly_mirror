import { useState } from "react";
import useFetch from "../hooks/useFetch";

export default function BoardList({ boards, loading, error }) {
  //   const [boards, setBoards] = useState([

  //       {
  //         id: 101,
  //         model: "Twofer",
  //         shaper: "Channel Islands",
  //         length: 6.0,
  //         width: 19.5,
  //         volume: 29.8,
  //         purchasedAt: "2025-01-01",
  //         owner: { id: 1 },
  //       },
  //       {
  //         id: 102,
  //         model: "Long Glide",
  //         shaper: "Firewire",
  //         length: 9.2,
  //         width: 22.0,
  //         volume: 68.4,
  //         purchasedAt: "2024-06-15",
  //         owner: { id: 1 },
  //       },
  //       {
  //         id: 103,
  //         model: "Fish Fry",
  //         shaper: "Lost",
  //         length: 5.8,
  //         width: 2
  //         volume: 35.2,
  //         purchasedAt: "2023-08-10",
  //         owner: { id: 1 },
  //       },
  //   ],);
  if (loading) return <p className="m-5">Loading boards...</p>;
  if (error) return <p className="m-5 text-red-500">Error: {error}</p>;
  if (!boards?.length) return <p className="m-5">No boards found.</p>;
  return (
    <div className="flex flex-wrap gap-3 m-5">
      {boards.map(({ id, model, shaper, length }) => (
        <BoardCard key={id} boardName={model} shaper={shaper} length={length} />
      ))}
    </div>
  );
}

const BoardCard = ({ boardName, shaper, length }) => {
  return (
    <div className="rounded-lg border shadow-sm overflow-hidden bg-shingle border-slate-200 shadow-slate-950/5 w-96">
      <img
        src="https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fcdn.shopify.com%2Fs%2Ffiles%2F1%2F0436%2F9222%2F8774%2Fproducts%2Fkookapinto-shapes-surfboards-kookapinto-shapes-7-0-thin-twin-warm-airbrush-fade-surfboard-surfbored-35196702228646.jpg%3Fv%3D1665424417&f=1&nofb=1&ipt=794cdf2c68416c7e2e4a462ee05fc86edb7985b8223b07c5aa80108c4bc59670&amp;ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&amp;auto=format&amp;fit=crop&amp;w=927&amp;q=80"
        alt="card-image"
        className="w-[calc(100%-16px)] rounded m-2 h-96 object-cover"
      />
      <div className="w-full h-max rounded px-3.5 py-2.5">
        <div className="mb-2 flex items-center justify-between">
          <h6 className="font-sans antialiased font-bold text-base md:text-lg lg:text-xl text-current">
            {boardName}
          </h6>
          <h6 className="font-sans antialiased font-bold text-base md:text-lg lg:text-xl text-current">
            {length}
          </h6>
        </div>
        <p className="font-sans antialiased text-base text-slate-600">
          {shaper}
        </p>
      </div>
      <div className="w-full px-3.5 pt-2 pb-3.5 rounded">
        <button
          className="inline-flex items-center justify-center border align-middle select-none font-sans font-medium text-center transition-all duration-300 ease-in disabled:opacity-50 disabled:shadow-none disabled:cursor-not-allowed data-[shape=pill]:rounded-full data-[width=full]:w-full focus:shadow-none text-sm rounded-md py-2 px-4 shadow-sm hover:shadow-md bg-slate-200 border-slate-200 text-slate-800 hover:bg-slate-100 hover:bg-slate-100"
          data-shape="default"
          data-width="full"
        >
          Log Sesh
        </button>
      </div>
    </div>
  );
};
