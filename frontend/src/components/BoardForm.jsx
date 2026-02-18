// {
//   "model": "rfboard",
//   "shaper": "uhhhh",
//   "length": 9.2,
//   "width": 20.5,
//   "volume": 32.5,
//   "owner": {
//     "id": 2
//   }
// }

//http://localhost:8080/api/v1/surfboard

import React, { useState } from "react";
import usePost from "../hooks/usePost";

const BoardForm = ({ isOpen, onClose, onBoardCreated }) => {
  const [formData, setFormData] = useState({
    model: "",
    shaper: "",
    length: "",
    width: "",
    volume: "",
  });

  const { execute, loading, error, success } = usePost(
    "http://localhost:8080/api/v1/surfboard",
  );

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // match API schema
    const payload = {
      ...formData,
      length: parseFloat(formData.length),
      width: parseFloat(formData.width),
      volume: parseFloat(formData.volume), // convert strings to number
      owner: {
        id: 2, // example owner
      },
    };

    // const result = await execute(formData);
    const result = await execute(payload);
    console.log("DEBUG: Result from server:", result);
    if (result) {
      onBoardCreated(result);
      setTimeout(() => {
        onClose();
      }, 1000);
    }
  };

  if (!isOpen) return null;

  return (
    /* The overlay is now transparent so the background remains fully visible */
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-transparent p-4">
      {/* Invisible "click-catcher" so clicking outside the form still closes it */}
      <div className="fixed inset-0 cursor-default" onClick={onClose}></div>

      {/* The Actual Form - Added 'shadow-2xl' and 'border' to make it pop */}
      <div className="relative bg-white rounded-xl shadow-[0_20px_50px_rgba(0,0,0,0.2)] w-full max-w-md overflow-hidden transform transition-all animate-in fade-in zoom-in duration-200">
        <div className="bg-night p-5 text-white flex justify-between items-center">
          <h2 className="text-lg font-bold uppercase">New Board</h2>
          <button
            onClick={onClose}
            className="hover:text-gray-200 cursor-pointer text-2xl leading-none"
          >
            &times;
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <div>
            <label className="block text-xs font-bold text-gray-500 uppercase mb-1">
              Model
            </label>
            <input
              name="model"
              onChange={handleChange}
              className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500 outline-none"
              required
            />
          </div>
          <div>
            <label className="block text-xs font-bold text-gray-500 uppercase mb-1">
              Shaper
            </label>
            <input
              name="shaper"
              onChange={handleChange}
              className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500 outline-none"
              required
            />
          </div>
          <div className="flex gap-4">
            <div className="flex-1">
              <label className="block text-xs font-bold text-gray-500 uppercase mb-1">
                Length
              </label>
              <input
                name="length"
                onChange={handleChange}
                className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500 outline-none"
                required
              />
            </div>
            <div className="flex-1">
              <label className="block text-xs font-bold text-gray-500 uppercase mb-1">
                Width
              </label>
              <input
                name="width"
                onChange={handleChange}
                className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500 outline-none"
                required
              />
            </div>
          </div>
          <div>
            <label className="block text-xs font-bold text-gray-500 uppercase mb-1">
              Volume (L)
            </label>
            <input
              type="number"
              step="0.1"
              name="volume"
              onChange={handleChange}
              className="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500 outline-none"
              required
            />
          </div>
          {/* Feedback Section */}
          {error && (
            <p className="text-red-500 text-sm font-medium">Error: {error}</p>
          )}
          {success && (
            <p className="text-green-500 text-sm font-medium">
              Board saved successfully!
            </p>
          )}
          <button
            type="submit"
            disabled={loading}
            className={`w-full py-3 rounded-xl font-bold text-white transition-all shadow-lg
              ${loading ? "bg-gray-400 cursor-not-allowed" : "bg-ocean hover:scale-105 active:scale-95 cursor-pointer shadow-blue-100"}`}
          >
            {loading ? "Saving..." : "Save Board"}
          </button>
        </form>
      </div>
    </div>
  );
};

export default BoardForm;
